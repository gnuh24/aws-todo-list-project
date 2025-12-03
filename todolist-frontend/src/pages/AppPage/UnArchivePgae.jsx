import React, { useEffect, useState } from "react";
import MainLayout from "../../layout/MainLayout";
import ProjectHeader from "../../component/Header/ProjectHeader";
import SectionItem from "../../component/Section/SectionItem";
import { useParams } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import { message } from "antd";
import { toast } from "sonner";

export default function UnArchivePage() {
  const [sections, setSections] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const { projectName, projectId } = useParams();

  // 🎯 Lấy sections + tasks trong sections
  useEffect(() => {
    const fetchSections = async () => {
      try {
        setLoading(true);

        const res = await https_taskflow.get(
          `/v1/projects/${projectId}/sections`
        );

        if (res.data?.status === 200 && Array.isArray(res.data.data)) {
          setSections(res.data.data);
        } else {
          setSections([]);
        }
      } catch (err) {
        setError("Không thể tải dữ liệu sections");
      } finally {
        setLoading(false);
      }
    };

    fetchSections();
  }, [projectId]);
  const handleUnarchive = async () => {
    try {
      const res = await https_taskflow.patch(`/v1/projects/${projectId}`, {
        isArchived: false,
      });

      if (res.data?.status === 200) {
        toast.success("Project unarchived");
        window.location.reload();
      }
    } catch (err) {
      console.error(err);
      toast.error("Unarchive failed");
    }
  };

  const isArchived = true;

  return (
    <MainLayout>
      <div className="min-h-screen bg-white">
        <ProjectHeader />

        <main className="px-10 py-6">
          <h1 className="text-2xl font-bold mb-4">{projectName}</h1>

          {/* ARCHIVE BANNER */}
          {isArchived && (
            <div className="bg-red-50 border px-5 py-4 rounded-lg flex justify-between items-center mb-6">
              <div className="flex items-center gap-2">
                <span className="text-gray-600">
                  📁 This project is archived
                </span>
              </div>

              <button
                onClick={handleUnarchive}
                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
              >
                Unarchive
              </button>
            </div>
          )}

          {/* SECTION LIST */}
          <div className="space-y-8">
            {sections.map((section) => (
              <div key={section.id}>
                {/* SECTION TITLE */}
                <h2 className="font-semibold text-gray-900 mb-2">
                  {section.name}
                  <span className="text-sm text-gray-500 ml-2">
                    {section.tasks?.length || 0}
                  </span>
                </h2>

                {/* TASK LIST */}
                <div className="space-y-3">
                  {section.tasks?.map((task) => (
                    <div
                      key={task.id}
                      className="flex items-start gap-2 py-2 border-b"
                    >
                      <div>
                        <div className="text-sm font-medium">{task.title}</div>
                        <div className="text-xs text-gray-500">
                          {task.description}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}

            {sections.length === 0 && (
              <p className="text-center text-gray-400 mt-10">
                No sections found.
              </p>
            )}
          </div>
        </main>
      </div>
    </MainLayout>
  );
}
