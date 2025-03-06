import { useAuthStore } from "@/store/authStore";

const BASE_URL = "/api/v1/user";


// ✅ 공통 요청 옵션 함수 (JWT 토큰 포함)
const getAuthHeaders = () => {
  const token = useAuthStore.getState().accessToken; // ✅ Zustand에서 accessToken 가져오기
  if (!token) {
    console.warn("❌ JWT 토큰 없음, 인증 필요");
    return null;
  }

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`, // ✅ JWT 토큰 포함
  };
};


// ✅ 프로젝트 생성 (POST)
export const createProject = async (projectData: FormData) => {
  console.log("📢 [createProject] 요청 데이터:", projectData);

  // ✅ 인증 헤더 가져오기
  const headers = await getAuthHeaders();
  if (!headers) {
    console.error("❌ 인증 실패 → 로그인 페이지로 이동 필요");
    return { resultCode: "401", message: "Unauthorized" };
  }

  try {
    const res = await fetch("http://localhost:8080/api/v1/user/project", {
      method: "POST",
      headers: {
        ...headers, // ✅ 인증 헤더 추가
        Accept: "application/json",
      },
      credentials: "include",
      body: projectData,
    });

    console.log("📢 [createProject] API 응답 상태 코드:", res.status);

    if (res.status === 415) {
      console.error("❌ 프로젝트 생성 실패: Content-Type이 지원되지 않음");
      return { resultCode: "415", message: "Unsupported Media Type" };
    }

    const data = await res.json();
    console.log("📢 [createProject] API 응답 데이터:", data);

    return data;
  } catch (error) {
    console.error("❌ 프로젝트 생성 중 오류 발생:", error);
    return { resultCode: "500", message: "Internal Server Error" };
  }
};



// ✅ 프로젝트 전체 조회 (GET)
export const getProjects = async (keyword: string = "") => {
  try {
    const url = keyword
      ? `http://localhost:8080/api/v1/user/projects?keyword=${encodeURIComponent(keyword)}`
      : "http://localhost:8080/api/v1/user/projects";

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", // ✅ 쿠키 포함 요청 추가
    });

    console.log("📢 [getProjects] API 응답 상태 코드:", response.status);

    if (response.status === 401) {
      console.warn("❌ 인증이 필요합니다.");
      return { code: "401", message: "Unauthorized" };
    }

    const data = await response.json();
    console.log("📢 [getProjects] API 응답 데이터:", data);

    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ 프로젝트 목록 조회 중 오류 발생:", error);
    return { code: "500", message: "Internal Server Error" };
  }
};



// ✅ 프로젝트 단건 조회 (GET)
export const getProjectById = async (projectId?: string) => {
  if (!projectId) {
    console.error("❌ getProjectById 호출 오류: projectId가 없습니다.");
    return { resultCode: "400", message: "잘못된 요청: projectId가 없습니다." };
  }

  try {
    const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      credentials: "include", // ✅ 쿠키에서 accessToken 자동 포함
    });

    console.log("📢 [getProjectById] API 응답 상태 코드:", res.status);

    if (!res.ok) {
      return { resultCode: res.status.toString(), message: "프로젝트 조회 실패" };
    }

    const responseData = await res.json();
    console.log("📢 [getProjectById] API 응답 데이터:", responseData);

    return { resultCode: responseData.resultCode, data: responseData.data }; 
  } catch (error) {
    console.error("❌ 프로젝트 조회 중 오류 발생:", error);
    return { resultCode: "500", message: "프로젝트 조회 중 오류가 발생했습니다." };
  }
};



// ✅ 프로젝트 수정 (PUT)
export const updateProject = async (projectId: string, projectData: any) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(projectData),
  });

  return res.json();
};

// ✅ 프로젝트 삭제 (DELETE)
export const deleteProject = async (projectId: string) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
    method: "DELETE",
    headers, // ✅ JWT 토큰 추가
  });

  return res.json();
};

// ✅ 프로젝트를 휴지통으로 이동
export const moveToTrash = async (projectIds: string[]) => {
  const headers = await getAuthHeaders(); // ✅ 인증 헤더 가져오기

  if (!headers) {
    console.error("❌ [moveToTrash] JWT 토큰 없음 → 로그인 페이지로 이동");
    window.location.href = "/login";
    return { code: "401", message: "Unauthorized" };
  }

  // ✅ 프로젝트 ID를 쿼리스트링으로 변환
  const queryString = projectIds.map(id => `projectIds=${id}`).join("&");

  try {
    const res = await fetch(`${BASE_URL}/projects?${queryString}`, { // ✅ 쿼리스트링으로 데이터 전달
      method: "DELETE",
      headers,
      credentials: "include", // ✅ 쿠키 포함 요청
    });

    if (!res.ok) {
      return { code: res.status.toString(), message: "휴지통 이동 실패" };
    }

    const data = await res.json();
    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ [moveToTrash] 휴지통 이동 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};



// ✅ 휴지통 목록 조회 (GET)
export const getTrashProjects = async () => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  try {
    const res = await fetch(`${BASE_URL}/projects/trash`, {
      method: "GET",
      headers,
      credentials: "include", // ✅ JWT 토큰 포함 요청
    });

    if (!res.ok) {
      return { code: res.status.toString(), message: "휴지통 조회 실패" };
    }

    const data = await res.json();
    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ [getTrashProjects] 휴지통 조회 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};

// ✅ 선택한 프로젝트 복원 (POST)
export const restoreProjects = async (projectIds: string[]) => {
  const headers = getAuthHeaders();
  if (!headers) return { code: "401", message: "Unauthorized" };

  try {
    const res = await fetch(`${BASE_URL}/projects/restore`, {
      method: "POST",
      headers,
      credentials: "include", // ✅ JWT 토큰 포함 요청
      body: JSON.stringify({ projectIds }),
    });

    if (!res.ok) {
      return { code: res.status.toString(), message: "복원 실패" };
    }

    const data = await res.json();
    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ [restoreProjects] 복원 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};
