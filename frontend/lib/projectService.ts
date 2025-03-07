const BASE_URL = "/api/v1/user";

// ✅ 프로젝트 생성 (POST)
export const createProject = async (formDataObj: FormData) => {
  try {
    const response = await fetch("http://localhost:8080/api/v1/user/projects", {
      method: "POST",
      body: formDataObj,
      credentials: "include",
    });

    const result = await response.json();
    console.log("📢 [createProject] API 응답:", result);

    return result;
  } catch (error) {
    console.error("❌ [createProject] 프로젝트 생성 중 오류 발생:", error);
    return { resultCode: "500", message: "서버 오류" };
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
       "Content-Type": "application/json",
        Accept: "application/json", //chatGPT가 알려준코드..ㅎ
      },
      credentials: "include", // ✅ 쿠키에서 accessToken 자동 포함
    });
    console.log(`data = ,${projectId}`);
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
export const updateProject = async (projectId: string, projectData: any, thumbnail?: File) => {
  try {
    const formData = new FormData();

    // ✅ projectId, thumbnailPath 제외하고 새로운 객체 생성
    const { projectId: _, thumbnailPath: __, ...filteredProjectData } = projectData;

    // ✅ JSON을 Blob으로 변환하여 추가
    formData.append(
      "projectRequest",
      new Blob([JSON.stringify(filteredProjectData)], { type: "application/json" })
    );

    // ✅ 썸네일이 있을 때만 추가
    if (thumbnail) {
      formData.append("thumbnail", thumbnail);
      formData.append("deleteThumbnail", "false");
    } else if (!projectData.thumbnailPath || projectData.thumbnailPath === "/default_project.png") {
      formData.append("deleteThumbnail", "true");
    } else {
      formData.append("deleteThumbnail", "false");
    }

    // 📢 FormData 확인 (중요)
    console.log("📢 [updateProject] 최종 전송 데이터:");
    for (let pair of formData.entries()) {
      console.log(`🔹 ${pair[0]}:`, pair[1]);
    };

    // ✅ PUT 요청 전송
    const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
      method: "PUT",
      credentials: "include",
      body: formData,
    });

    console.log("📢 [updateProject] API 응답 상태 코드:", res.status);

    if (!res.ok) {
      return { resultCode: res.status.toString(), message: "프로젝트 업데이트 실패" };
    }

    const responseData = await res.json();
    console.log("📢 [updateProject] API 응답 데이터:", responseData);

    return responseData;
  } catch (error) {
    console.error("❌ 프로젝트 업데이트 중 오류 발생:", error);
    return { resultCode: "500", message: "프로젝트 업데이트 중 오류가 발생했습니다." };
  }
};
 

// ✅ 프로젝트 삭제 (DELETE)
export const deleteProject = async (projectId: string) => {
  try {
    const res = await fetch(`${BASE_URL}/projects/${projectId}`, {
      method: "DELETE",
      credentials: "include", // ✅ 쿠키 포함 요청
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) {
      return { code: res.status.toString(), message: "삭제 실패" };
    }

    return { code: "200", message: "삭제 성공" };
  } catch (error) {
    console.error("❌ [deleteProject] 프로젝트 삭제 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};


// ✅ 프로젝트를 휴지통으로 이동
export const moveToTrash = async (projectIds: string[]) => {
  const queryString = projectIds.map(id => `projectIds=${id}`).join("&");

  try {
    const res = await fetch(`${BASE_URL}/projects?${queryString}`, {
      method: "DELETE",
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
  try {
    const res = await fetch(`${BASE_URL}/projects/trash`, {
      method: "GET",
      credentials: "include", // ✅ 쿠키 포함 요청
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


// ✅ 선택한 프로젝트 복구 (POST)
export const restoreProjects = async (projectIds: number[]) => {
  try {
    // ✅ 쿼리스트링으로 변환 (예: ?projectIds=28&projectIds=34)
    const queryString = projectIds.map(id => `projectIds=${id}`).join("&");

    const res = await fetch(`${BASE_URL}/projects/restore?${queryString}`, { 
      method: "POST",
      credentials: "include", // ✅ 쿠키 포함 요청
    });

    if (!res.ok) {
      const errorData = await res.json();
      console.error("❌ 복구 실패: ", errorData);
      return { code: res.status.toString(), message: errorData.message || "복구 실패" };
    }

    const data = await res.json();
    return { code: "200", data: data.data };
  } catch (error) {
    console.error("❌ [restoreProjects] 복원 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};

export const permanentlyDeleteProjects = async (projectIds: number[]) => {
  try {
    // ✅ 쿼리스트링 변환 (예: ?projectIds=28&projectIds=34)
    const queryString = projectIds.map(id => `projectIds=${id}`).join("&");

    const res = await fetch(`${BASE_URL}/projects/permanent?${queryString}`, { 
      method: "DELETE",
      credentials: "include", // ✅ 쿠키 포함 요청
    });

    if (!res.ok) {
      const errorData = await res.json();
      console.error("❌ 완전 삭제 실패: ", errorData);
      return { code: res.status.toString(), message: errorData.message || "삭제 실패" };
    }

    return { code: "200", message: "삭제 성공" };
  } catch (error) {
    console.error("❌ [permanentlyDeleteProjects] 삭제 중 오류 발생:", error);
    return { code: "500", message: "서버 오류" };
  }
};
