
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

/** 게시글 타입 정의 */
export interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt?: string;  // 수정일자 (optional)
  userId?: number;  // 작성자 ID도 optional 처리
}

interface PostList {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  boards: Post[];
}

/** 공통 응답 객체 */
interface RsData<T> {
  code: string;
  message: string;
  data: T;
}

/** 공통 요청 옵션 (쿠키 기반) */
const getRequestOptions = (method: string, body?: any) => ({
  method,
  headers: {
    "Content-Type": "application/json",
  },
  credentials: "include" as const, // 쿠키 포함
  body: body ? JSON.stringify(body) : undefined,
});

// 게시글 목록 조회 (GET /api/v1/user/boards)
export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards?page=${page}&size=${size}`, getRequestOptions("GET"));
    
    if (!res.ok) {
      if (res.status === 400) {
        throw new Error("게시글이 존재하지 않습니다.");
      }
      throw new Error(`게시글 목록 불러오기 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<PostList> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 목록 조회 실패:", error);
    throw error;
  }
};

// 게시글 상세 조회 (GET /api/v1/user/boards/{id})
export const getPostById = async (id: number): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("GET"));
    
    if (!res.ok) {
      if (res.status === 400) {
        throw new Error("해당 게시글이 존재하지 않습니다.");
      }
      throw new Error(`게시글 조회 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 상세 조회 실패:", error);
    throw error;
  }
};

// 게시글 작성 (POST /api/v1/user/boards)
export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards`, getRequestOptions("POST", postData));

    if (!res.ok) {
      throw new Error(`게시글 작성 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 작성 실패:", error);
    throw error;
  }
};

// 게시글 수정 (PATCH /api/v1/user/boards/{id})
export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("PATCH", postData));

    if (!res.ok) {
      throw new Error(`게시글 수정 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<Post> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 수정 실패:", error);
    throw error;
  }
};

//게시글 삭제 (DELETE /api/v1/user/boards/{id})
export const deletePost = async (id: number): Promise<{ message: string }> => {
  try {
    const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, getRequestOptions("DELETE"));

    if (!res.ok) {
      throw new Error(`게시글 삭제 실패: ${res.status} ${res.statusText}`);
    }

    const data: RsData<{ message: string }> = await res.json();
    return data.data;
  } catch (error) {
    console.error("❌ 게시글 삭제 실패:", error);
    throw error;
  }
// export const deletePost = async (id: number, userId: number): Promise<{ message: string }> => {
//   try {
//     console.log("🛠 게시글 삭제 요청:", id, userId);

//     const res = await fetch(`${API_URL}/api/v1/user/boards/${id}`, {
//       method: "DELETE",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       credentials: "include", // ✅ JWT 포함 (필수)
//       body: JSON.stringify({ userId }), // ✅ 요청 바디 추가
//     });

//     console.log("🛠 게시글 삭제 응답 상태 코드:", res.status);

//     if (!res.ok) {
//       throw new Error(`게시글 삭제 실패: ${res.status} ${res.statusText}`);
//     }

//     const data: RsData<{ message: string }> = await res.json();
//     return data.data;
//   } catch (error) {
//     console.error("❌ 게시글 삭제 요청 실패:", error);
//     throw error;
//   }
};



