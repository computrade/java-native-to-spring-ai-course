package com.computrade.course.spring.ai.stream.client.controller;


import com.computrade.course.spring.ai.stream.client.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stream")
public class ChatStreamController {

    private final ChatService chatService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(String prompt) {
        return Mono.fromCallable(() -> chatService.chat(prompt))
                .subscribeOn(Schedulers.boundedElastic()) // Phase 1: Offload setup
                .flatMapMany(flux -> flux);               // Phase 2: Stream tokens live
    }


//    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> chat(@RequestParam String prompt) {
//        return Flux.defer(() -> chatService.chat(prompt))
//                .subscribeOn(Schedulers.boundedElastic()) ;// Offloads tool resolution off the Netty thread
//                //.flatMapMany(flux -> flux);
//    }



//    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> chat(@RequestParam String prompt) {
//        return Flux.defer(() -> {
//            // This log and chatService.chat() call are NOW GUARANTEED
//            // to run on boundedElastic, not the Netty thread!
//            log.info("Executing chat on thread: {}", Thread.currentThread().getName());
//            return chatService.chat(prompt);
//        }).subscribeOn(Schedulers.boundedElastic());
//    }

}
