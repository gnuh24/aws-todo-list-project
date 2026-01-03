import { CheckOutlined } from "@ant-design/icons";
import { useEffect, useState } from "react";
import { https_taskflow } from "../../service/api";
import { toast } from "sonner";
import AvatarCircle from "../../component/Content/AvatarCircle";

export default function MemberDropdown({
    open,
    taskDetail,
    onAssign,
    onClose,
}) {
    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!open) return;

        const loadMembers = async () => {
            try {
                setLoading(true);
                const res = await https_taskflow.get(
                    `/v1/projects/${taskDetail.idProject}/members`
                );
                setMembers(res.data.data || []);
            } catch {
                toast.error("Failed to load project members");
            } finally {
                setLoading(false);
            }
        };

        loadMembers();
    }, [open, taskDetail.idProject]);

    if (!open) return null;

    return (
        <div className="absolute right-0 mt-2 w-64 bg-white border rounded-lg shadow-lg z-50 max-h-80 overflow-auto">
            {loading && (
                <div className="p-3 text-sm text-gray-500">Loading...</div>
            )}

            {!loading &&
                members.map(member => {
                    const isAssigned =
                        taskDetail.accountAssign?.id === member.accountId;

                    return (
                        <div
                            key={member.accountId}
                            className={`px-3 py-2 flex items-center gap-3 cursor-pointer
                            ${isAssigned
                                    ? "bg-blue-100 text-blue-700"
                                    : "hover:bg-gray-100"
                                }`}
                            onClick={() => {
                                if (isAssigned) return;
                                onAssign(member.accountId);
                                onClose();
                            }}
                        >
                            {/* ✅ Avatar with fallback */}
                            <AvatarCircle
                                avatar={member.avatar}
                                name={member.displayName}
                                size={24}
                            />


                            <span className="flex-1 text-sm truncate">
                                {member.displayName}
                            </span>

                            {isAssigned && <CheckOutlined />}
                        </div>
                    );
                })}

            {/* Remove assignee */}
            <div
                className="px-3 py-2 text-sm text-red-500 cursor-pointer hover:bg-red-50"
                onClick={() => {
                    onAssign(null);
                    onClose();
                }}
            >
                Remove assignee
            </div>
        </div>
    );
}
