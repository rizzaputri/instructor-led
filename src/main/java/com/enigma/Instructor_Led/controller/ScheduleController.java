package com.enigma.Instructor_Led.controller;

import com.enigma.Instructor_Led.dto.request.CreateScheduleRequest;
import com.enigma.Instructor_Led.dto.request.UpdateScheduleRequest;
import com.enigma.Instructor_Led.dto.response.CommonResponse;
import com.enigma.Instructor_Led.dto.response.DocumentationImageResponse;
import com.enigma.Instructor_Led.dto.response.ScheduleResponse;
import com.enigma.Instructor_Led.entity.Schedule;
import com.enigma.Instructor_Led.service.ImageKitService;
import com.enigma.Instructor_Led.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ImageKitService imageKitService;

    @PostMapping
    public ResponseEntity<CommonResponse<ScheduleResponse>> createSchedule(
            @RequestBody CreateScheduleRequest request
    ) {
        ScheduleResponse schedule = scheduleService.create(request);
        CommonResponse<ScheduleResponse> response = CommonResponse
                .<ScheduleResponse>builder()
                .message("Schedule created successfully")
                .statusCode(HttpStatus.CREATED.value())
                .data(schedule)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CommonResponse<ScheduleResponse>> updateSchedule(
            @RequestBody UpdateScheduleRequest request
    ) {
        ScheduleResponse schedule = scheduleService.update(request);
        CommonResponse<ScheduleResponse> response = CommonResponse
                .<ScheduleResponse>builder()
                .message("Schedule updated successfully")
                .statusCode(HttpStatus.OK.value())
                .data(schedule)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentationImageResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") String id

    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Handle case when the file is empty
        }
        DocumentationImageResponse imageResponse = imageKitService.uploadImage(file, id);
        return ResponseEntity.ok(imageResponse);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<ScheduleResponse>> getById(
            @PathVariable("id") String id
    ) {
        Schedule schedule = scheduleService.getById(id);
        ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                .id(schedule.getId())
                .date(schedule.getDate())
                .topic(schedule.getTopic())
                .trainerId(schedule.getTrainer().getId())
                .programmingLanguageId(schedule.getProgrammingLanguage().getId())
                .build();
        CommonResponse<ScheduleResponse> response = CommonResponse
                .<ScheduleResponse>builder()
                .message("Schedule fetched succesfully")
                .statusCode(HttpStatus.OK.value())
                .data(scheduleResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ScheduleResponse>>> getAllSchedules(
            @RequestParam(name = "language", required = false) String language,
            @RequestParam(name = "start-date", required = false) String startDate,
            @RequestParam(name = "end-date", required = false) String endDate
    ) {
        List<ScheduleResponse> schedules = scheduleService.getAll(language, startDate, endDate);
        CommonResponse<List<ScheduleResponse>> response = CommonResponse
                .<List<ScheduleResponse>>builder()
                .message("Schedule fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .data(schedules)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/trainee/{id}")
    public ResponseEntity<CommonResponse<List<ScheduleResponse>>> getAllByTraineeId(
            @PathVariable String id
    ) {
        List<ScheduleResponse> schedules = scheduleService.getAllByTraineeId(id);
        CommonResponse<List<ScheduleResponse>> response = CommonResponse
                .<List<ScheduleResponse>>builder()
                .message("Schedule fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .data(schedules)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/trainer/{id}")
    public ResponseEntity<CommonResponse<List<ScheduleResponse>>> getAllByTrainerId(
            @PathVariable String id
    ) {
        List<ScheduleResponse> schedules = scheduleService.getAllByTrainerId(id);
        CommonResponse<List<ScheduleResponse>> response = CommonResponse
                .<List<ScheduleResponse>>builder()
                .message("Schedule fetched successfully")
                .statusCode(HttpStatus.OK.value())
                .data(schedules)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CommonResponse<?>> deleteSchedule(
            @PathVariable String id
    ) {
        scheduleService.delete(id);
        CommonResponse<?> response = CommonResponse
                .builder()
                .message("Schedule deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
