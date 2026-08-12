package com.computrade.course.spring.ai.stream.controller;


import com.computrade.course.spring.ai.stream.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
                .flatMapMany(flux -> flux);    // Phase 2: Stream tokens live
    }

}
