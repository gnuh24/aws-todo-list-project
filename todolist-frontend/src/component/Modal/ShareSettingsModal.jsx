import { useState, useRef, useEffect } from "react";
import { ChevronDown, Copy, Lock, User, X } from "lucide-react";
import { https_taskflow, https_user } from "../../service/api";
import { useParams } from "react-router-dom";
import { toast } from "sonner";
import AvatarCircle from "../Content/AvatarCircle";
import { useProjectContext } from "../../context/ProjectContext";

export default function ShareSettings({ onClose }) {
    const { projectId } = useParams();

    const { members, setMembers } = useProjectContext();


    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [userFound, setUserFound] = useState(null);

    const [openDropdown, setOpenDropdown] = useState(null);

    const dropdownRef = useRef();

    // -----------------------------------
    // FETCH MEMBERS
    // -----------------------------------
    useEffect(() => {
        const fetchMembers = async () => {
            try {
                const res = await https_taskflow.get(`/v1/projects/${projectId}/members`);
                const members = res.data?.data || [];
                setMembers(members);
            } catch (err) {
                console.error("FAILED MEMBER LIST:", err);
            }
        };

        fetchMembers();
    }, [projectId]);


    const handleCopyLink = async () => {
        try {
            const res = await  https_taskflow.post(`/v1/projects/${projectId}/invites`);

            const inviteLink = res.data.data.inviteUrl;

            console.error(inviteLink);

            await navigator.clipboard.writeText(inviteLink);

            toast.success("Đã copy link mời vào project 🔗");
        } catch (err) {
            toast.error("Không thể tạo link mời");
        }
    };

    // -----------------------------------
    // SEARCH USER WHEN TYPING EMAIL
    // -----------------------------------
    useEffect(() => {
        if (!email) {
            setUserFound(null);
            return;
        }

        const delay = setTimeout(async () => {
            try {
                setLoading(true);
                const res = await https_user.get(`/v1/accounts?email=${email}`);
                console.log(res.data.data.id)
                if (res.data?.data) {
                    setUserFound(res.data.data);
                    console.log(userFound)
                } else {
                    setUserFound(null);
                }
            } catch (err) {
                setUserFound(null);
            } finally {
                setLoading(false);
            }
        }, 500);

        return () => clearTimeout(delay);
    }, [email]);


    // -----------------------------------
    // INVITE USER
    // -----------------------------------
    const handleInviteUser = async () => {
        if (!userFound) return;

        try {
            const body = {
                idAccount: userFound.id,
                role: "MEMBER",
            };

            await https_taskflow.post(
                `/v1/projects/${projectId}/members`,
                body
            );

            toast.success("User invited successfully!");

            setEmail("");
            setUserFound(null);

            // ❌ KHÔNG setMembers ở đây
            // ✔ Members sẽ được add qua websocket:
            // EVENT.PROJECT_MEMBER_ADDED / ACCEPTED

        } catch (err) {
            console.error("ERROR INVITING USER:", err);
            toast.error("Thành viên đã có trong dự án");
        }
    };



    // -----------------------------------
    // REMOVE MEMBER
    // -----------------------------------
    const removeCollaborator = async (memberId) => {
        try {
            await https_taskflow.delete(`/v1/projects/${projectId}/members/${memberId}`);

            setMembers(members.filter((c) => c.id !== memberId));
            toast.success("Member removed");
            setOpenDropdown(null);
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="fixed top-16 right-6 w-[480px] bg-white rounded-xl shadow-2xl overflow-y-auto z-50 animate-slideIn">
            <div className="bg-white rounded-2xl shadow-xl p-6 animate-fadeIn relative">

                {/* Close */}
                <button className="absolute top-4 right-4 text-gray-600 hover:text-gray-800"
                    onClick={onClose}>
                    <X size={20} />
                </button>

                {/* Input search */}
                <input
                    type="text"
                    placeholder="Add people by name or email"
                    className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                {/* Loading */}
                {loading && (
                    <div className="mt-3 text-sm text-gray-500">Searching...</div>
                )}

                {/* Found user */}
                {userFound && !loading && (
                    <div
                        className="mt-3 p-3 bg-gray-50 rounded-xl border cursor-pointer hover:bg-gray-100"
                        onClick={() => handleInviteUser()}
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
                            <div>
                                <div className="text-sm font-semibold">{userFound.displayName}</div>
                                <div className="text-xs text-gray-500">{userFound.email}</div>
                            </div>
                        </div>
                    </div>
                )}

                {/* No user found */}
                {!loading && email && !userFound && (
                    <div className="mt-3 p-3 text-sm text-gray-600 border rounded-xl">
                        No user found
                    </div>
                )}


                {/* Members */}
                <div className="mt-6">
                    <div className="text-xs font-semibold text-gray-700 mb-2">In this project</div>

                    {members.map((c, i) => (
                        <div key={i} className="flex items-center justify-between py-3 border-b last:border-none">

                            <div className="flex items-center gap-3">
                                <AvatarCircle
                                    avatar={c.avatar}
                                    name={c.displayName}
                                    size={36}
                                />


                                <div>
                                    <div className="text-sm font-medium">{c.displayName}</div>
                                    <div className="text-xs text-gray-500">{c.email}</div>
                                    {c.status === "PENDING" && (
                                        <div className="text-xs text-gray-400">Pending</div>
                                    )}
                                </div>
                            </div>

                            {c.role === "OWNER" ? (
                                <span className="text-xs text-gray-500">{c.role}</span>
                            ) : (
                                <div ref={dropdownRef} className="relative">
                                    <button
                                        className="flex items-center gap-1 text-sm text-gray-700 hover:text-black"
                                        onClick={() => setOpenDropdown(openDropdown === i ? null : i)}
                                    >
                                        {c.role}
                                        <ChevronDown size={16} />
                                    </button>

                                    {openDropdown === i && (
                                        <div className="absolute right-0 mt-2 w-48 bg-white border rounded-xl shadow-lg py-2">
                                            <button
                                                className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                                                onClick={() => removeCollaborator(c.id)}
                                            >
                                                Remove from project
                                            </button>
                                        </div>
                                    )}
                                </div>
                            )}

                        </div>
                    ))}
                </div>

                {/* Footer */}
                <div className="flex justify-between items-center mt-6 pt-4 border-t">
                    <button onClick={handleCopyLink} className="flex items-center gap-2 text-sm border px-3 py-2 rounded-md">
                        <Copy size={16} /> Copy link
                    </button>
                </div>
            </div>
        </div>
    );
}
