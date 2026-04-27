@import org.example.model.IndexModel
@param IndexModel model

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${model.title()}</title>
    <!-- HTMX -->
    <script src="https://unpkg.com/htmx.org@2.0.4" integrity="sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+" crossorigin="anonymous"></script>
    <style>
        *, *::before, *::after { box-sizing: border-box; }

        body {
            font-family: system-ui, sans-serif;
            margin: 0;
            padding: 2rem;
            background: #f8f9fa;
            color: #212529;
        }

        header {
            border-bottom: 2px solid #dee2e6;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
        }

        h1 { margin: 0; }

        button {
            padding: .5rem 1.25rem;
            border: none;
            border-radius: 4px;
            background: #0d6efd;
            color: #fff;
            font-size: 1rem;
            cursor: pointer;
        }

        button:hover { background: #0b5ed7; }

        #content {
            margin-top: 1.5rem;
            padding: 1rem;
            background: #fff;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            min-height: 4rem;
        }

        .htmx-indicator {
            display: none;
            color: #6c757d;
            font-style: italic;
        }

        .htmx-request .htmx-indicator { display: inline; }
    </style>
</head>
<body>
    <header>
        <h1>${model.title()}</h1>
    </header>

    <main>
        <p>Welcome! Click the button below to load content without a full page reload.</p>

        <button
            hx-get="/hello"
            hx-target="#content"
            hx-swap="innerHTML"
            hx-indicator="#spinner">
            Load greeting
        </button>
        <span id="spinner" class="htmx-indicator">Loading&hellip;</span>

        <div id="content">
            <em>No content loaded yet.</em>
        </div>
    </main>
</body>
</html>
