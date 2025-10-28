import React, { useEffect, useState } from "react";

function TestAPI() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Giả sử bạn lưu token vào localStorage sau khi login
  const token =
    "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ0eXAiOiJhY2Nlc3MiLCJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3NTk4OTA0NTEsImV4cCI6MTc2MjQ4MjQ1MX0.e79L2tjVtk3ZN1UOhs8c61PwYuxIpZXIS1AYXCK2FKE";

  const domain =
    process.env.REACT_APP_TASKFLOW_SERVICE_DOMAIN ||
    "http://localhost:8082/api/taskflow";

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await fetch(`${domain}/v1/projects`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error(`Lỗi: ${response.status}`);
        }

        const data = await response.json();
        setProjects(data.data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, [domain, token]);

  if (loading) return <p>Đang tải danh sách project...</p>;
  if (error) return <p style={{ color: "red" }}>Lỗi: {error}</p>;

  return (
    <div>
      <h2>Danh sách project</h2>
      <ul>
        {projects.map((p) => (
          <li key={p.id}>{p.name}</li>
        ))}
      </ul>
    </div>
  );
}

export default TestAPI;
