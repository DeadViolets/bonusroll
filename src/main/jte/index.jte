@import org.example.model.IndexModel
@param IndexModel model

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${model.title()}</title>
    <link rel="stylesheet" href="/static/styles.css"/>
    <script src="https://unpkg.com/htmx.org@2.0.4" integrity="sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+" crossorigin="anonymous"></script>
</head>
<body>
    <header>
        <h1>${model.title()}</h1>
        <p class="subtitle">Compute your optimal bonus roll targets from raidbots sim reports.</p>
    </header>

    <main>
        <form
            hx-post="/simulate"
            hx-target="#results"
            hx-swap="innerHTML"
            hx-indicator="#spinner">

            <fieldset>
                <legend>Raidbots Report URLs</legend>

                <div class="field">
                    <label for="singleBossUrl">Single Boss Report</label>
                    <input
                        id="singleBossUrl"
                        name="singleBossUrl"
                        type="url"
                        placeholder="https://www.raidbots.com/simbot/report/…"
                        autocomplete="off"
                        spellcheck="false"/>
                </div>

                <div class="field">
                    <label for="multiBossUrl">Multiple Bosses Report</label>
                    <input
                        id="multiBossUrl"
                        name="multiBossUrl"
                        type="url"
                        placeholder="https://www.raidbots.com/simbot/report/…"
                        autocomplete="off"
                        spellcheck="false"/>
                </div>

                <div class="field">
                    <label for="dungeonUrl">Dungeons Report</label>
                    <input
                        id="dungeonUrl"
                        name="dungeonUrl"
                        type="url"
                        placeholder="https://www.raidbots.com/simbot/report/…"
                        autocomplete="off"
                        spellcheck="false"/>
                </div>
            </fieldset>

            <div class="form-actions">
                <button type="submit">Compute bonus rolls</button>
                <span id="spinner" class="htmx-indicator">Fetching reports&hellip;</span>
            </div>
        </form>

        <section id="results" aria-live="polite">
            <%-- Results will be injected here by HTMX --%>
        </section>
    </main>
</body>
</html>
