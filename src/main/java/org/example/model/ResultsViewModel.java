package org.example.model;

import java.util.List;

/**
 * View model passed to the results Jte fragment.
 *
 * @param reports  One entry per submitted URL (in submission order).
 *                 Contains either a parsed result or an error message.
 */
public record ResultsViewModel(List<ReportEntry> reports) {

    public record ReportEntry(
            String label,
            String url,
            ReportResult result,   // null when error is set
            String error           // null when result is set
    ) {
        public boolean hasError() { return error != null; }
    }
}
