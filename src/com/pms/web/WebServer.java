package com.pms.web;

import com.pms.export.PDFExporter;
import com.pms.model.Profile;
import com.pms.service.DataStorageException;
import com.pms.service.ProfileManager;
import com.pms.service.ValidationException;
import com.pms.template.ResumeTemplate;
import com.pms.template.TemplateType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebServer
 * Embedded HTTP web server powered by standard Java SE (jdk.httpserver).
 * Serves the responsive Web UI and exposes REST API endpoints for profile CRUD and PDFBox export.
 * 
 * URL to access: http://localhost:8080
 */
public class WebServer {

    public static final int DEFAULT_PORT = 8080;
    private final int port;
    private final ProfileManager profileManager;
    private HttpServer server;

    public WebServer(int port) {
        this.port = port;
        this.profileManager = new ProfileManager();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Endpoints
        server.createContext("/api/profiles", new ProfilesApiHandler());
        server.createContext("/api/sample", new SampleApiHandler());
        server.createContext("/api/export-pdf", new ExportPdfApiHandler());

        // Static Web Content Handler
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        System.out.println("==========================================================");
        System.out.println(" Profile Management Web Application is LIVE!");
        System.out.println(" Access URL: http://localhost:" + port);
        System.out.println("==========================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            WebServer webServer = new WebServer(port);
            webServer.start();
        } catch (IOException e) {
            System.err.println("Failed to start web server on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Static Web Assets Handler ---
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("web" + path);
            if (!file.exists() || file.isDirectory()) {
                sendResponse(exchange, 404, "text/plain", "File not found: " + path);
                return;
            }

            String contentType = getContentType(path);
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=utf-8";
            if (path.endsWith(".css")) return "text/css; charset=utf-8";
            if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (path.endsWith(".json")) return "application/json; charset=utf-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".svg")) return "image/svg+xml";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }

    // --- REST API: /api/profiles ---
    private class ProfilesApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod().toUpperCase();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                switch (method) {
                    case "GET":
                        handleGet(exchange);
                        break;
                    case "POST":
                        handlePost(exchange);
                        break;
                    case "PUT":
                        handlePut(exchange);
                        break;
                    case "DELETE":
                        handleDelete(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            String query = params.get("q");
            String id = params.get("id");

            if (id != null && !id.isEmpty()) {
                Profile p = profileManager.getProfileById(id);
                if (p != null) {
                    sendResponse(exchange, 200, "application/json", JsonHelper.profileToJson(p));
                } else {
                    sendResponse(exchange, 404, "application/json", "{\"error\":\"Profile not found\"}");
                }
                return;
            }

            List<Profile> list = (query != null && !query.trim().isEmpty())
                    ? profileManager.searchProfilesByName(query)
                    : profileManager.getAllProfiles();

            sendResponse(exchange, 200, "application/json", JsonHelper.profileListToJson(list));
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            Profile p = JsonHelper.jsonToProfile(body);
            try {
                profileManager.addProfile(p);
                sendResponse(exchange, 201, "application/json", JsonHelper.profileToJson(p));
            } catch (ValidationException ve) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(ve.getMessage()) + "\"}");
            } catch (DataStorageException dse) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(dse.getMessage()) + "\"}");
            }
        }

        private void handlePut(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            Profile p = JsonHelper.jsonToProfile(body);
            try {
                profileManager.updateProfile(p);
                sendResponse(exchange, 200, "application/json", JsonHelper.profileToJson(p));
            } catch (ValidationException ve) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(ve.getMessage()) + "\"}");
            } catch (DataStorageException dse) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(dse.getMessage()) + "\"}");
            }
        }

        private void handleDelete(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            String id = params.get("id");
            if (id == null || id.isEmpty()) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing profile id\"}");
                return;
            }
            try {
                boolean deleted = profileManager.deleteProfile(id);
                if (deleted) {
                    sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Profile deleted successfully\"}");
                } else {
                    sendResponse(exchange, 404, "application/json", "{\"error\":\"Profile not found\"}");
                }
            } catch (DataStorageException dse) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"" + JsonHelper.escapeJson(dse.getMessage()) + "\"}");
            }
        }
    }

    // --- REST API: /api/sample ---
    private class SampleApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            Profile sample = ProfileManager.createSampleProfile();
            sendResponse(exchange, 200, "application/json", JsonHelper.profileToJson(sample));
        }
    }

    // --- REST API: /api/export-pdf ---
    private class ExportPdfApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            String id = params.get("id");
            String tplName = params.get("template");

            Profile profile;
            if (id != null && !id.isEmpty()) {
                profile = profileManager.getProfileById(id);
            } else {
                profile = ProfileManager.createSampleProfile();
            }

            if (profile == null) {
                sendResponse(exchange, 404, "application/json", "{\"error\":\"Profile not found\"}");
                return;
            }

            TemplateType type = TemplateType.PROFESSIONAL;
            if (tplName != null) {
                try {
                    type = TemplateType.valueOf(tplName.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            File tempPdf = File.createTempFile("Resume_", ".pdf");
            try {
                ResumeTemplate template = ResumeTemplate.getTemplate(type);
                PDFExporter exporter = new PDFExporter(profile, template);
                exporter.exportToFile(tempPdf);

                byte[] pdfBytes = Files.readAllBytes(tempPdf.toPath());
                String filename = "Resume_" + profile.getFullName().trim().replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + type.name() + ".pdf";

                exchange.getResponseHeaders().set("Content-Type", "application/pdf");
                exchange.getResponseHeaders().set("Content-Disposition", "inline; filename=\"" + filename + "\"");
                exchange.sendResponseHeaders(200, pdfBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(pdfBytes);
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"PDF generation failed: " + JsonHelper.escapeJson(e.getMessage()) + "\"}");
            } finally {
                tempPdf.delete();
            }
        }
    }

    // --- Utilities ---
    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    map.put(key, val);
                } catch (Exception ignored) {
                }
            }
        }
        return map;
    }
}
