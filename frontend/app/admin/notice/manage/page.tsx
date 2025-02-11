"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import axios from 'axios';
import { useAuthStore } from '@/store/authStore'; // Zustand 스토어 사용
import styles from '../noticeList.module.css';

type NoticeDetailResponse = {
  id: number;
  subject: string;
  content: string;
  createdAt: string;
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const NoticeManagePage = () => {
  const [notices, setNotices] = useState<NoticeDetailResponse[]>([]);
  const router = useRouter();
  const { isLoggedIn, login } = useAuthStore();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');

    if (token) {
      if (!isLoggedIn) {
        // 토큰이 있지만 로그인 상태가 아닐 경우 로그인 상태 업데이트
        login(token);
      }
    } else {
      // 토큰이 없을 경우 로그인 페이지로 리다이렉트
      router.push('/admin/login');
      return;
    }

    const fetchNotices = async () => {
      try {
        const response = await axios.get<RsData<NoticeDetailResponse[]>>('/api/v1/common/notices', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (Array.isArray(response.data.data)) {
          setNotices(response.data.data);
        } else {
          console.error('Data is not an array:', response.data.data);
        }
      } catch (error) {
        console.error('Error fetching notices:', error);
      }
    };

    fetchNotices();
  }, [router, isLoggedIn, login]);

  const handleDelete = async (id: number) => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No access token found');
      return;
    }

    const confirmDelete = window.confirm('해당 공지글을 삭제하시겠습니까?');
    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`/api/v1/admin/notices/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setNotices(notices.filter(notice => notice.id !== id));
      alert('공지사항이 성공적으로 삭제되었습니다!');
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error('Error deleting notice:', error.response?.data || error.message);
        alert(`삭제 실패: ${error.response?.data?.message || '알 수 없는 오류가 발생했습니다.'}`);
      } else {
        console.error('Unexpected error:', error);
        alert('삭제 실패: 알 수 없는 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">공지사항 관리</h1>
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded-lg shadow-md hover:bg-blue-600"
          onClick={() => router.push("/admin/notice/create")}
        >
          작성하기
        </button>
      </div>
      <ul>
        {notices.map((notice) => (
          <li key={notice.id} className={styles.noticeBox} onClick={() => router.push(`/admin/notice/${notice.id}`)}>
            <div className={styles.noticeSubjectRow}>
              <div className={styles.noticeSubject}>
                {notice.subject}
              </div>
              <div className={styles.noticeDate}>
                {new Date(notice.createdAt).toLocaleDateString('ko-KR')}
              </div>
            </div>
            <p className={styles.noticeContent}>{notice.content}</p>
            <div className="flex justify-end space-x-2 mt-2">
              <button
                className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
                onClick={(e) => {
                  e.stopPropagation();
                  router.push(`/admin/notice/edit/${notice.id}`);
                }}
              >
                수정
              </button>
              <button
                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete(notice.id);
                }}
              >
                삭제
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NoticeManagePage;
