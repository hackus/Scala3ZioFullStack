//import { defineConfig } from "vite";
//import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";
//
//export default defineConfig({
//  plugins: [scalaJSPlugin()],
//});


import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
    base: "/",
    plugins: [scalaJSPlugin(
        {
            cwd: "..",
            projectID: "client"
        }
    )],
    server: {
        proxy: {
            '/api': 'http://localhost:9000',
        },
    },
});