package com.pofo.backend.domain.notice.service;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.notice.dto.reponse.NoticeCreateResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeUpdateResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.dto.request.NoticeUpdateRequest;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.exception.NoticeException;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeCreateResponse create(NoticeCreateRequest noticeCreateRequest, Admin admin) {

        if (admin == null) {
            throw new UnauthorizedActionException("관리자 정보가 유효하지 않습니다.");
        }

        try {
            Notice notice = Notice.builder()
                    .admin(admin)
                    .subject(noticeCreateRequest.getSubject())
                    .content(noticeCreateRequest.getContent())
                    .build();

            this.noticeRepository.save(notice);
            return new NoticeCreateResponse(notice.getId());
        } catch (Exception e) {
            throw new NoticeException("공지사항 생성 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public NoticeUpdateResponse update(Long id, NoticeUpdateRequest noticeUpdateRequest, Admin admin) {

        Notice notice = this.noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeException("해당 공지사항을 찾을 수 없습니다."));

        if (!notice.getAdmin().equals(admin)) {
            throw new UnauthorizedActionException("공지사항을 수정할 권한이 없습니다.");
        }

        try {
            notice.update(noticeUpdateRequest.getSubject(), noticeUpdateRequest.getContent());
            return new NoticeUpdateResponse(notice.getId());
        } catch (NoticeException e) {
            throw new NoticeException("공지사항 수정 중 오류가 발생했습니다. 원인: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id, Admin admin) {

        Notice notice = this.noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeException("해당 공지사항을 찾을 수 없습니다."));

        if (notice.getAdmin().equals(admin)) {
            try {
                this.noticeRepository.delete(notice);
            } catch (Exception e) {
                throw new NoticeException("공지사항 삭제 중 오류가 발생했습니다. 원인: " + e.getMessage());
            }
        } else {
            throw new UnauthorizedActionException("공지사항을 삭제할 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse findById(Long id) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeException("해당 공지사항을 찾을 수 없습니다."));

        return new NoticeDetailResponse(notice.getId(), notice.getSubject(), notice.getContent(), notice.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<NoticeDetailResponse> findAll() {

        List<Notice> notices = this.noticeRepository.findAllByOrderByCreatedAtDesc();
        return notices.stream()
                .map(notice -> new NoticeDetailResponse(notice.getId(), notice.getSubject(), notice.getContent(), notice.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long count() {
        return this.noticeRepository.count();
    }
}
