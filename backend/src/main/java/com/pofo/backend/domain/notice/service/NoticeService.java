package com.pofo.backend.domain.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import com.pofo.backend.domain.notice.dto.reponse.NoticeCreateResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.dto.reponse.NoticeUpdateResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.dto.request.NoticeUpdateRequest;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.exception.NoticeNotFoundException;
import com.pofo.backend.domain.notice.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

	@Transactional
	public NoticeCreateResponse create(NoticeCreateRequest noticeCreateRequest) {

		Notice notice = Notice.builder()
			.subject(noticeCreateRequest.getSubject())
			.content(noticeCreateRequest.getContent())
			.build();

		noticeRepository.save(notice);
		return new NoticeCreateResponse(notice);
	}

	@Transactional
	public NoticeUpdateResponse update(Long id, NoticeUpdateRequest noticeUpdateRequest) {

		Notice notice = this.noticeRepository.findById(id)
			.orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다."));
		notice.update(noticeUpdateRequest.getSubject(), noticeUpdateRequest.getContent());

		return new NoticeUpdateResponse(notice);
	}

	@Transactional
	public void delete(Long id) {

		Notice notice = this.noticeRepository.findById(id)
			.orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다."));

		this.noticeRepository.delete(notice);
	}

	@Transactional(readOnly = true)
	public NoticeDetailResponse findById(Long id) {

		Notice notice = noticeRepository.findById(id)
			.orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다."));

		return new NoticeDetailResponse(notice);
	}

	@Transactional(readOnly = true)
	public List<NoticeDetailResponse> findAll() {

		List<Notice> notices = this.noticeRepository.findAllByOrderByCreatedAtDesc();
		return notices.stream()
			.map(NoticeDetailResponse::new)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Long count() {
		return this.noticeRepository.count();
	}
}
