"use client";


import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '../../../../utils/api';
import { useAuthStore } from '@/store/authStore';
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

    const fetchNotices = async () => {
      try {
        const response = await api.get<RsData<NoticeDetailResponse[]>>('/common/notices');
        if (Array.isArray(response.data.data)) {
          setNotices(response.data.data);
        } else {
          console.error('Data is not an array:', response.data.data);
        }
      } catch (error) {
        console.error('Error fetching notices:', error);
        // 인증 실패 시 로그인 페이지로 이동
        router.push('/admin/login');
      }
    };

    fetchNotices();
  }, [router]);

  const handleDelete = async (id: number) => {
    const confirmDelete = window.confirm('해당 공지글을 삭제하시겠습니까?');
    if (!confirmDelete) {
      return;
    }

    try {
      await api.delete(`/admin/notices/${id}`, { withCredentials: true });
      setNotices(notices.filter(notice => notice.id !== id));
      alert('공지사항이 성공적으로 삭제되었습니다!');
    } catch (error) {
      console.error('Error deleting notice:', error);
      alert('삭제 실패: 알 수 없는 오류가 발생했습니다.');
    }
  };

  const handleCreateNotice = () => {
    router.push('/admin/notice/create');
  };

  return (
    <div className={styles.noticeContainer}>
      <h1 className={styles.noticeHeader} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        공지사항 관리
        <button
          onClick={handleCreateNotice}
          className={styles.createButton}
          style={{
            backgroundColor: '#0070f3',
            color: '#ffffff',
            padding: '10px 15px',
            fontSize: '16px',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer'
          }}
        >
          작성하기
        </button>
      </h1>
      <hr style={{ margin: '20px 0' }} />
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
