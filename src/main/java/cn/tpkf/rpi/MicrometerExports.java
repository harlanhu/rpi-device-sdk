package cn.tpkf.rpi;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * Starts a simple HTTP endpoint that exposes Prometheus metrics using Micrometer.
 */
public final class MicrometerExports {

    private final PrometheusMeterRegistry registry;
    private com.sun.net.httpserver.HttpServer server;

    private MicrometerExports() {
        this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    public static MicrometerExports start(int port, IntSupplier currentDeviceCount, LongSupplier totalRegistered, LongSupplier shutdownOps) throws IOException {
        MicrometerExports exports = new MicrometerExports();
        // register gauges and counters
        Gauge.builder("rpi_devices.current", currentDeviceCount::getAsInt)
                .description("Currently registered devices")
                .register(exports.registry);
        Gauge.builder("rpi_devices.total_registered", totalRegistered::getAsLong)
                .description("Cumulative devices registered")
                .register(exports.registry);
        Gauge.builder("rpi_devices.shutdown_ops", shutdownOps::getAsLong)
                .description("Total shutdown operations")
                .register(exports.registry);

        // start HTTP server
        exports.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        exports.server.createContext("/metrics", httpExchange -> {
            String response = exports.registry.scrape();
            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            httpExchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        exports.server.setExecutor(null);
        exports.server.start();
        return exports;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
