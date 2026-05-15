<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Bonus Roll</title>
    <link rel="stylesheet" href="/static/styles.css"/>
    <script src="https://unpkg.com/htmx.org@2.0.4" integrity="sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+" crossorigin="anonymous"></script>
</head>
<body>
    <header>
        <h1>Bonus Roll</h1>
        <p class="subtitle">Compute your optimal bonus roll targets from Raidbots sim reports.</p>
    </header>

    <main>
        <form
            hx-post="/simulate"
            hx-target="#results"
            hx-swap="innerHTML">

            <div class="input-container">
                <fieldset>
                    <legend>Raidbots Report URLs</legend>

                    <div class="field">
                        <label for="voidspire">Voidspire</label>
                        <input
                                id="voidspire"
                                name="voidspire"
                                type="url"
                                placeholder="https://www.raidbots.com/simbot/report/…"
                                autocomplete="off"
                                spellcheck="false"/>
                    </div>

                    <div class="field">
                        <label for="dreamrift">Dreamrift</label>
                        <input
                                id="dreamrift"
                                name="dreamrift"
                                type="url"
                                placeholder="https://www.raidbots.com/simbot/report/…"
                                autocomplete="off"
                                spellcheck="false"/>
                    </div>

                    <div class="field">
                        <label for="march">March On Quel'Danas</label>
                        <input
                                id="march"
                                name="march"
                                type="url"
                                placeholder="https://www.raidbots.com/simbot/report/…"
                                autocomplete="off"
                                spellcheck="false"/>
                    </div>

                    <div class="field">
                        <label for="dungeon">Dungeons</label>
                        <input
                                id="dungeon"
                                name="dungeon"
                                type="url"
                                placeholder="https://www.raidbots.com/simbot/report/…"
                                autocomplete="off"
                                spellcheck="false"/>
                    </div>
                </fieldset>

                <div class="search">
                    <input
                            type="text"
                            name="query"
                            placeholder="Search items..."
                            hx-get="/items/search"
                            hx-trigger="keyup changed delay:200ms"
                            hx-target="#search-results"
                            hx-swap="innerHTML"
                            autocomplete="off"
                    >
                    <div id="search-results"></div>
                    <div>
                        <h4>Selected items</h4>
                        <ul id="selected-items"></ul>
                    </div>
                    <div id="selected-inputs"></div>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit">Compute bonus rolls</button>
            </div>
        </form>

        <section id="results" aria-live="polite">
        </section>
    </main>
</body>
</html>
