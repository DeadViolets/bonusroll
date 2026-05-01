@import org.example.model.ResultsViewModel
@param ResultsViewModel model

<article class="report">
    <h2 class="report-title">Funsies</h2>

    <table class="items-table">
        <thead>
            <tr>
                <th>Source</th>
                <th>Weight</th>
                <th>Items</th>
            </tr>
        </thead>
        <tbody>
            @for(var row : model.reportEntries())
                <tr>
                    <td>${row.name()}</td>
                    <td>${String.format("%.0f", row.weight())}</td>
                    <td>
                        <div class="items-row">
                            @for(var item : row.items())
                                <div class="item">
                                    <span class="icon-container">
                                        <img class="icon" src="/static/icons/${item.iconName()}.png" alt="${item.name()}">
                                        <span class="icon-tooltip">${item.name()} ${item.id()}</span>
                                    </span>
                                    <p>${String.format("%.0f", item.dps())}</p>
                                </div>
                            @endfor
                        </div>
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
</article>
