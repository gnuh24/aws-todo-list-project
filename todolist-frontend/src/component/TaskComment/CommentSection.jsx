import { useCallback,useMemo, useState, useEffect, useRef } from "react";
import {Input, Button, message, Avatar} from "antd";
import {
    PaperClipOutlined,
    UpOutlined,
    DeleteOutlined, EditOutlined, CopyOutlined
} from "@ant-design/icons";
import dayjs from "dayjs";
import {BASE_URL, https_media, https_taskflow} from "../../service/api";
import { CommentAttachItem } from "../CommentAttach/CommentAttachItem";
import { CommentAttachItemAdd } from "../CommentAttach/CommentAttachItemAdd";
import SpinnerForSettings from "../Spinner/SpinnerForSettings";
import { toast } from "sonner";
import AvatarCircle from "../Content/AvatarCircle";

const MAX_SIZE = 3 * 1024 * 1024;

export default function CommentSection({ isOpenComment, comments, setTaskDetail, taskDetail }) {
    /* ===================== STATE ===================== */
    const [newComment, setNewComment] = useState("");
    const [attachments, setAttachments] = useState([]);
    const [showComments, setShowComments] = useState(isOpenComment ?? false);
    const [showEditForm, setShowEditForm] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);
    const [openMenu, setOpenMenu] = useState(null);
    const [loading, setLoading] = useState(false);

    const auth = useMemo(() => {
        try {
            return JSON.parse(localStorage.getItem("USER_INFO"));
        } catch {
            return null;
        }
    }, []);

    const formatToDisplay = "HH:mm DD/MM";

    const containerRef = useRef(null);
    const menuRef = useRef(null);
    const fileInputRef = useRef(null);

    /* ===================== EFFECT ===================== */
    useEffect(() => {
        if (showComments && containerRef.current) {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
        }
    }, [showComments, comments?.length, loading]);

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setOpenMenu(null);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    useEffect(() => {
        setShowComments(isOpenComment);
    }, [isOpenComment]);

    /* ===================== HELPER ===================== */
    const handleApiError = (err, fallback = "Đã xảy ra lỗi") => {
        const msg =
            err?.response?.data?.message ||
            err?.response?.data?.detailMessage ||
            fallback;
        toast.error(msg);
    };

    /* ===================== COMMENT ACTIONS ===================== */
    const handleComment = useCallback(async (comment, urls) => {
        const res = await https_taskflow.post(
            `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/comments`,
            { comment, urls }
        );

        setTaskDetail(prev => ({
            ...prev,
            comments: [...prev.comments, res.data.data],
        }));
    }, [taskDetail.id, taskDetail.idProject, setTaskDetail]);

    const onUpdateComment = useCallback(async (comment, id) => {

        const res = await https_taskflow.patch(
            `/v1/projects/${taskDetail.idProject}/tasks/comments/${id}`,
            { comment }
        );

        setTaskDetail(prev => ({
            ...prev,
            comments: prev.comments.map(c =>
                c.id === res.data.data.id ? res.data.data : c
            ),
        }));
    }, [taskDetail.idProject, setTaskDetail]);

    const onDeleteComment = useCallback(async (id) => {
        await https_taskflow.delete(
            `/v1/projects/${taskDetail.idProject}/tasks/comments/${id}`
        );

        setTaskDetail(prev => ({
            ...prev,
            comments: prev.comments.filter(c => c.id !== id),
        }));
    }, [taskDetail.idProject, setTaskDetail]);

    /* ===================== SUBMIT ===================== */
    const handleSubmit = async () => {
        if (!newComment.trim()) {
            toast.warning("Không bỏ trống nội dung comment");
            return;
        }

        setLoading(true);
        try {
            await handleComment(newComment, attachments.length ? attachments : null);
            setNewComment("");
            setAttachments([]);
            setIsExpanded(false);
        } catch (err) {
            handleApiError(err, "Không thể tạo comment");
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async (id) => {
        if (!newComment.trim()) {
            showEditForm(null);
            return;
        }

        setLoading(true);
        try {
            await onUpdateComment(newComment, id);
            setNewComment("");
            setShowEditForm(null);
        } catch (err) {
            handleApiError(err, "Không thể cập nhật comment");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Bạn có muốn xóa comment này không?")) return;

        setLoading(true);
        try {
            await onDeleteComment(id);
        } catch (err) {
            handleApiError(err, "Không thể xóa comment");
        } finally {
            setLoading(false);
        }
    };

    /* ===================== FILE UPLOAD ===================== */
    const handleFileSelect = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        if (file.size > MAX_SIZE) {
            message.error(`❌ File "${file.name}" vượt quá 3MB`);
            e.target.value = "";
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        try {
            const res = await https_media.post("/v1/local/uploads", formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            // mỗi lần add đúng 1 file
            setAttachments(prev => [...prev, res.data.data]);
        } catch (err) {
            toast.error("Upload file thất bại");
        } finally {
            // reset để chọn lại cùng file nếu cần
            e.target.value = "";
        }
    };


    const deleteCommentAttach = async (url) => {
        if (!window.confirm("Bạn có muốn xóa ảnh này?")) return;

        try {
            const res = await https_taskflow.delete(
                `/v1/projects/${taskDetail.idProject}/deleteCommentAttach`,
                { params: { fileUrl: url } }
            );

            const deleted = res.data?.data;
            if (!deleted) return;

            setTaskDetail(prev => ({
                ...prev,
                comments: (prev.comments || []).map(comment =>
                    comment.id === deleted.taskCommentId
                        ? {
                            ...comment,
                            commentAttach: (comment.commentAttach || []).filter(
                                att => att.id !== deleted.id
                            )
                        }
                        : comment
                )
            }));

        } catch (err) {
            const msg =
                err?.response?.data?.message ||
                err?.response?.data?.detailMessage ||
                "Không thể xóa file đính kèm";

            toast.error(msg);
        }
    };




    return (
        <div className="w-full border-t pt-10" ref={menuRef}>

            {loading && (
                <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-50">
                    <SpinnerForSettings />{/* Hoặc component Spinner của bạn */}
                </div>
            )}

            {!loading && <>
                <input
                    type="file"
                    ref={fileInputRef}
                    className="hidden"
                    onChange={handleFileSelect}
                />
                {/* Header */}
                <div className="flex items-center gap-2 mb-3 cursor-pointer select-none" onClick={() => setShowComments(!showComments)}>
                    <UpOutlined
                        className={`transition-transform duration-400 ${showComments ? 'rotate-180' : 'rotate-90'}`}
                    />
                    <span className="font-medium text-gray-700">Comments</span>
                    <span className="text-sm text-gray-500">{comments.length}</span>
                </div>

                {/* Comment List */}
                <div
                    ref={containerRef}
                    className={`space-y-4 mb-4 pr-2 transition-all duration-300 overflow-y-auto ${showComments ? "max-h-80" : "max-h-0 overflow-hidden"
                        }`}

                >
                    {comments.map((c) => {
                        const isMe = c.accountId === auth.id;

                        return (
                            <div
                                key={c.id}
                                className="flex gap-2 py-2 rounded-lg"
                            >

                                {/* Avatar */}
                                <AvatarCircle avatar={c.authorAvatar} name={c.authorName}  size={36}/>

                                {/* Comment box */}
                                <div className="flex-1">
                                    {showEditForm !== c.id ? (
                                        <div
                                            className="relative rounded-lg px-5 pb-4 shadow-sm bg-white group"
                                        >
                                            <div className="text-sm font-semibold text-gray-600 mb-1">{c.authorName}</div>
                                            <div className="text-xs text-gray-600 mb-1">{dayjs(c.updatedAt).format(formatToDisplay)}</div>
                                            <div className="text-sm text-gray-700 whitespace-pre-line pt-3">{c.comment}</div>
                                            {!!c.commentAttach?.length && <CommentAttachItem commentAttach={c.commentAttach} onDeleteCommentAttach={deleteCommentAttach} />}

                                            {/* Menu button */}
                                            <div className="absolute top-2 right-2">
                                                <button
                                                    onClick={() => setOpenMenu(openMenu === c.id ? null : c.id)}
                                                    className="w-8 h-8 flex items-center justify-center text-gray-600 bg-gray-100  rounded-md text-2xl"
                                                >
                                                    ⋯
                                                </button>

                                                {openMenu === c.id && (
                                                    <div className="absolute top-12 right-0 w-36 bg-white border rounded-lg shadow-lg z-20">
                                                        {isMe && (
                                                            <>
                                                                <button
                                                                    className="w-full text-left px-3 py-2 hover:bg-gray-100 text-sm flex items-center gap-2"
                                                                    onClick={() => {
                                                                        setShowEditForm(c.id);
                                                                        setNewComment('');
                                                                        setOpenMenu(null);
                                                                    }}
                                                                >
                                                                    <EditOutlined className="text-base" /> Edit
                                                                </button>
                                                                <button
                                                                    className="w-full text-left px-3 py-2 hover:bg-gray-100 text-sm text-red-500 flex items-center gap-2"
                                                                    onClick={() => {
                                                                        setShowEditForm(null);
                                                                        setNewComment('');
                                                                        handleDelete(c.id);
                                                                        setOpenMenu(null);
                                                                    }}
                                                                >
                                                                    <DeleteOutlined className="text-base" /> Delete
                                                                </button>
                                                            </>
                                                        )}
                                                        <button
                                                            className="w-full text-left px-3 py-2 hover:bg-gray-100 text-sm flex items-center gap-2"
                                                            onClick={() => {
                                                                navigator.clipboard.writeText(c.comment);
                                                                toast.success('Copied to clipboard!');
                                                                setOpenMenu(null);
                                                            }}
                                                        >
                                                            <CopyOutlined className="text-base" /> Copy text
                                                        </button>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="rounded-md p-3 bg-white w-full border">
                                            <Input.TextArea
                                                rows={3}
                                                defaultValue={c.comment}
                                                onChange={(e) => setNewComment(e.target.value)}
                                                className="border-none focus:ring-0 resize-none w-full"
                                                autoFocus
                                            />
                                            <div className="flex justify-end gap-2 mt-2">
                                                <Button onClick={() => setShowEditForm(null)}>Cancel</Button>
                                                <Button type="primary" danger onClick={() => handleUpdate(c.id)}>
                                                    Update
                                                </Button>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>

                        );

                    })}

                </div>


                {/* Comment Input */}
                {!isExpanded && (
                    <div className="w-full flex items-center gap-2">

                        <AvatarCircle avatar={auth.avatar} name={auth.name} size={36} />

                        <div className="flex-1 flex items-center gap-2 border rounded-full px-3 py-2 hover:bg-gray-100" onClick={() => {
                            setIsExpanded(true)
                            setShowEditForm(null)
                            setNewComment("") // Reset lại new Comment trong trường hợp đang chỉnh sửa comment mà cancel
                        }
                        }>
                            <button
                                type="button"
                                className="w-full text-left text-gray-500 hover:bg-gray-100 rounded-lg text-sm"
                            >
                                Write a comment...
                            </button>
                        </div>
                    </div>
                )}

                {isExpanded && (
                    <div className="border rounded-md p-3">
                        <Input.TextArea
                            rows={3}
                            placeholder="Comment"
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            className="border-none focus:ring-0 resize-none"
                            autoFocus
                            onKeyDown={(e) => {
                                if (e.key === "Enter" && !e.shiftKey) {
                                    e.preventDefault(); // tránh xuống dòng
                                    handleSubmit();
                                }
                            }}
                        />

                        {attachments.length > 0 && (
                            <CommentAttachItemAdd attachments={attachments} setAttachments={setAttachments} />
                        )}

                        <div className="flex justify-between items-center mt-2">
                            <div className="flex gap-3 text-gray-400 text-lg">
                                <PaperClipOutlined
                                    className="cursor-pointer text-gray-500 hover:text-gray-700"
                                    onClick={() => fileInputRef.current?.click()}
                                />
                            </div>
                            <div className="flex gap-2">
                                <Button onClick={() => {
                                    setIsExpanded(false)
                                    setNewComment("")
                                    setAttachments([])
                                }
                                }>Cancel</Button>
                                <Button type="primary" danger onClick={handleSubmit}>
                                    Comment
                                </Button>
                            </div>
                        </div>
                    </div>
                )}
            </>}
        </div>
    );
}
