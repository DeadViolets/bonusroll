package org.example.resource;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.database.Database;
import org.example.model.IndexModel;

@Path("/")
public class IndexResource {

    private final TemplateEngine templateEngine;

    @Inject
    public IndexResource(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        var output = new StringOutput();
        templateEngine.render("index.jte", Map.of(), output);
        return output.toString();
    }

    private List<IndexModel.FilterItem> getDatabaseItems(String filter) {
        List<IndexModel.FilterItem> filterItems = new ArrayList<>();
        try (Connection conn = Database.getDataSource().getConnection();
                PreparedStatement ps =
                        conn.prepareStatement("SELECT * FROM items WHERE name ILIKE ?")) {
            ps.setString(1, "%" + filter + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filterItems.add(
                            new IndexModel.FilterItem(
                                    List.of(rs.getInt("id")),
                                    rs.getString("name"),
                                    rs.getString("icon")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }

        Map<String, List<IndexModel.FilterItem>> groupedItems =
                filterItems.stream().collect(Collectors.groupingBy(IndexModel.FilterItem::name));

        List<IndexModel.FilterItem> mergedItems =
                groupedItems.values().stream()
                        .map(
                                itemList -> {
                                    List<Integer> mergedIds =
                                            itemList.stream()
                                                    .flatMap(fi -> fi.ids().stream())
                                                    .toList();
                                    return new IndexModel.FilterItem(
                                            mergedIds,
                                            itemList.getFirst().name(),
                                            itemList.getFirst().icon());
                                })
                        .toList();

        return mergedItems;
    }
}
