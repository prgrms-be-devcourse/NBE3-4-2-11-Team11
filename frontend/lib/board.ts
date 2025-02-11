import api from '../utils/api';  // 토큰 자동 처리 및 인터셉터 설정된 API 클라이언트 사용
import { RsData } from '../utils/types';
import { jwtDecode } from 'jwt-decode'; // JWT 디코딩 라이브러리 추가
import { getAccessToken } from '../utils/token';  // 토큰 가져오기

/** 게시글 타입 정의 */
interface Post {
  id: number;  // 게시글 ID
  title: string;
  content: string;
  createdAt: string;  // 작성일자 필드 추가
  nickname: string;  // 작성자 닉네임 추가
}

interface PostList {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  boards: Post[];
}

// JWT에서 사용자 닉네임 추출 함수
const extractNicknameFromToken = (): string | null => {
  const token = getAccessToken();
  if (!token) return null;

  try {
    const decoded: { nickname: string } = jwtDecode(token);
    return decoded.nickname;  // 디코딩된 토큰에서 닉네임 반환
  } catch (error) {
    console.error('토큰 디코딩 실패:', error);
    return null;
  }
};

// 게시글 목록 조회
export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
  const response = await api.get<RsData<PostList>>('/user/boards', { params: { page, size } });
  return response.data.data;
};

// 게시글 상세 조회 (특정 게시글 ID로 조회)
export const getPostById = async (id: number): Promise<Post> => {
  const response = await api.get<RsData<Post>>(`/user/boards/${id}`);
  return response.data.data;
};

// 게시글 작성 (로그인한 사용자의 닉네임으로 작성)
export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
  const token = getAccessToken();
  if (!token) throw new Error('로그인한 사용자 정보를 찾을 수 없습니다.');

  try {
    const decoded: { nickname: string } = jwtDecode(token);  // JWT에서 닉네임 추출
    const nickname = decoded.nickname;

    const response = await api.post<RsData<Post>>('/user/boards', { ...postData, nickname });
    return response.data.data;
  } catch (error) {
    console.error('토큰 디코딩 실패:', error);
    throw new Error('닉네임 추출에 실패했습니다.');
  }
};

// 게시글 수정 (특정 게시글 ID로 수정)
export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
  const response = await api.patch<RsData<Post>>(`/user/boards/${id}`, postData);
  return response.data.data;
};

// 게시글 삭제 (특정 게시글 ID로 삭제)
export const deletePost = async (id: number): Promise<{ message: string }> => {
  const response = await api.delete<RsData<{ message: string }>>(`/user/boards/${id}`);
  return response.data.data;
};
