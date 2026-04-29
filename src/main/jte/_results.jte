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
                            @for(Double dps : row.items())
                                <p>${String.format("%.0f", dps)}</p>
                            @endfor
                        </div>
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
</article>
