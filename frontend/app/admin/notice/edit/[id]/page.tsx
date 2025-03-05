'use client';

import axios from "axios";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useParams } from "next/navigation";

type NoticeUpdateResponse = {
    id: number;
};

const NoticeEditPage = () => {
  const { id } = useParams();
  const [notice, setNotice] = useState<{ subject: string; content: string }>({ subject: "", content: "" });
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const fetchNotice = async () => {

      try {
        const response = await axios.get<NoticeUpdateResponse>(`/api/v1/common/notices/${id}`, { withCredentials: true });
        setNotice(response.data.data);
      } catch (error) {
        console.error('Error fetching notice:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchNotice();
  }, [id]);

  const handleUpdate = async (updatedData) => {

    try {
      const response = await axios.patch(`/api/v1/admin/notices/${id}`, updatedData, { withCredentials: true });
      alert('공지사항이 성공적으로 수정되었습니다!');
      router.push("/admin/notice/manage");
    } catch (error) {
      console.error('Error updating notice:', error);
      alert('수정 실패: 알 수 없는 오류가 발생했습니다.');
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    handleUpdate(notice);
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">공지사항 수정</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label className="block mb-2">제목</label>
          <input
            type="text"
            value={notice.subject}
            onChange={(e) => setNotice({ ...notice, subject: e.target.value })}
            className="border p-2 w-full"
            required
          />
        </div>
        <div className="mt-4">
          <label className="block mb-2">내용</label>
          <textarea
            value={notice.content}
            onChange={(e) => setNotice({ ...notice, content: e.target.value })}
            className="border p-2 w-full"
            required
          />
        </div>
        <button type="submit" className="bg-blue-500 text-white px-4 py-2 mt-4 rounded">
          수정하기
        </button>
      </form>
    </div>
  );
};

export default NoticeEditPage;
