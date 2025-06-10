package com.ws.bebetter.service;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.FeedbackDto;
import com.ws.bebetter.web.dto.FeedbackSearchRequest;
import org.springframework.data.domain.Page;

/**
 * Сервис для управления отзывами о специалистах.
 */
public interface FeedbackService {

    /**
     * Создает новый отзыв.
     *
     * @param user специалист, которому добавляют отзыв.
     * @param feedbackDto DTO с данными для добавления нового отзыва.
     */
    void createFeedback(User user, FeedbackDto feedbackDto);

    /**
     * Получает все отзывы специалиста.
     *
     * @param user специалист, по которому запрашиваются отзывы.
     * @param searchRequest параметры пагинации.
     * @return {@link Page} с элементами {@link FeedbackDto}.
     */
    Page<FeedbackDto> getFeedbackList(User user, FeedbackSearchRequest searchRequest);

}