@import org.example.model.ResultsViewModel
@import org.example.model.ResultsViewModel.ReportEntry
@import org.example.model.ReportResult.AugmentedItem
@import org.example.model.ReportResult.SourceType
@param ResultsViewModel model

@for(ReportEntry report : model.reports())
    <article class="report">
        <h2 class="report-title">${report.label()}</h2>
        <p class="report-url"><a href="${report.url()}" target="_blank" rel="noopener">${report.url()}</a></p>

        @if(report.hasError())
            <p class="error">${report.error()}</p>
        @else
            <p class="base-dps">Baseline DPS: <strong>${String.format("%.0f", report.result().baseMeanDps())}</strong></p>

            <table class="items-table">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Source</th>
                        <th class="num">Mean DPS</th>
                        <th class="num">DPS Delta</th>
                    </tr>
                </thead>
                <tbody>
                    @for(AugmentedItem item : report.result().items())
                        <tr class="${item.sourceType() == SourceType.DUNGEON ? "row-dungeon" : "row-raid"}">
                            <td>${item.itemName()}</td>
                            <td>${item.source()}</td>
                            <td class="num">${String.format("%.0f", item.meanDps())}</td>
                            <td class="num ${item.dpsDelta() >= 0 ? "positive" : "negative"}">
                                ${item.dpsDelta() >= 0 ? "+" : ""}${String.format("%.0f", item.dpsDelta())}
                            </td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        @endif
    </article>
@endfor
