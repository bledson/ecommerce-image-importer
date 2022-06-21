package br.com.bledson.ecommerce.imageimporter.adapter.in.web;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

@RequestMapping("images")
@RestController
public class ImageController {

    @PostMapping("/{id}")
    void saveImages(@PathVariable("id") Long id, @RequestBody Collection<String> images) {
        Flux.fromIterable(images)
            .subscribeOn(Schedulers.boundedElastic())
            .map(s -> {
                String[] stringParts = s.split("\\.");
                String fileExtension = stringParts[stringParts.length - 1];
                Flux<DataBuffer> dataBufferFlux = WebClient.create()
                    .get()
                    .uri(s)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);
                DataBufferUtils.write(dataBufferFlux, Paths.get(System.getProperty("java.io.tmpdir") + File.separator + id + "-" + UUID.randomUUID() + "." + fileExtension)).subscribe();
                return s;
            }).subscribe();
    }
}
