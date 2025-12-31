import { CheckOutlined } from "@ant-design/icons";
import { useEffect, useState } from "react";
import { https_taskflow } from "../../service/api";
import { toast } from "sonner";

export default function ProjectSectionDropdown({
    open,
    onClose,
    taskDetail,
    onUpdateSection,
}) {
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!open) return;

        const loadProjects = async () => {
            try {
                setLoading(true);
                const res = await https_taskflow.get("/v1/projects");
                setProjects(res.data.data || []);
            } catch {
                toast.error("Failed to load projects");
            } finally {
                setLoading(false);
            }
        };

        loadProjects();
    }, [open]);

    if (!open) return null;

    return (
        <div className="absolute right-0 mt-2 w-64 bg-white border rounded-lg shadow-lg z-50 max-h-96 overflow-auto">
            {loading && (
                <div className="p-3 text-sm text-gray-500">Loading...</div>
            )}

            {!loading &&
                projects.map(project => {
                    const isCurrentProject = project.id === taskDetail.idProject;

                    return (
                        <div key={project.id} className="border-b last:border-b-0">
                            <div
                                className={`px-3 py-2 font-semibold flex justify-between
                ${isCurrentProject ? "bg-blue-50 text-blue-700" : "bg-gray-50"}`}
                            >
                                <span>{project.name}</span>
                                {isCurrentProject && <CheckOutlined />}
                            </div>

                            {project.section?.map(section => {
                                const isCurrentSection =
                                    isCurrentProject && section.id === taskDetail.idSection;

                                return (
                                    <div
                                        key={section.id}
                                        className={`px-4 py-2 text-sm cursor-pointer flex justify-between
                    ${isCurrentSection ? "bg-blue-100 text-blue-700" : "hover:bg-gray-100"}`}
                                        onClick={() => {
                                            if (isCurrentSection) return;
                                            onUpdateSection(project.id, section.id);
                                            onClose();
                                        }}
                                    >
                                        <span>▸ {section.name}</span>
                                        {isCurrentSection && <CheckOutlined />}
                                    </div>
                                );
                            })}
                        </div>
                    );
                })}
        </div>
    );
}
