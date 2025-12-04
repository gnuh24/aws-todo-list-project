import { useState, useEffect, useRef } from "react";
import {Input, Button, message} from "antd";
import {
    PaperClipOutlined,
    UpOutlined,
    DeleteOutlined, EditOutlined, CopyOutlined
} from "@ant-design/icons";
import dayjs from "dayjs";
import {https_taskflow} from "../../service/api";
import {CommentAttachItem} from "../CommentAttach/CommentAttachItem";
import {CommentAttachItemAdd} from "../CommentAttach/CommentAttachItemAdd";
import SpinnerForSettings from "../Spinner/SpinnerForSettings";
import {toast} from "sonner";

const MAX_SIZE = 3 * 1024 * 1024;

export default function CommentSection({ isOpenComment, comments, handleComment, onUpdateComment, onDeleteComment, onDeleteCommentAttach }) {
    const [newComment, setNewComment] = useState("");
    const [attachments, setAttachments] = useState([]); // list URL trả về từ backend
    const [showComments, setShowComments] = useState(isOpenComment ?? false);
    const [showEditForm, setShowEditForm] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);
    const [openMenu, setOpenMenu] = useState(null);
    const [loading, setLoading] = useState(false);

    const [auth] = useState(() => {
        const raw = localStorage.getItem("USER_INFO");
        if (!raw) return null;
        try { return JSON.parse(raw); } catch { return null; }
    });
    const formatToDisplay = "HH:mm DD/MM"
    const containerRef = useRef(null);
    const menuRef = useRef(null);
    const fileInputRef = useRef(null);


    useEffect(() => {
        if (showComments && containerRef.current) {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
        }
    }, [showComments, comments.length]); // scroll khi mở hoặc có comment mới

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setOpenMenu(null); // đóng menu
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    useEffect(() => {
        setShowComments(isOpenComment);
    }, [isOpenComment]);


    const handleSubmit = async () => {
        setLoading(true);
        if (!newComment.trim()) {
            alert("Không bỏ trống nội dung comment")
            return
        }
        await handleComment(newComment, attachments.length > 0 ? attachments : null);
        setAttachments([]);
        setNewComment("");
        setLoading(false);
    };

    const handleUpdate = async (idComment) => {
        setLoading(true);
        if (!newComment.trim()) {
            setShowEditForm(null);
            return
        }
        await onUpdateComment(newComment, idComment);
        setNewComment("");
        setShowEditForm(null);
        setLoading(false);
    };

    const handleDelete = async (idComment) => {
        if (!window.confirm("Bạn có muốn xóa comment này không?")) return
        setLoading(true);
        await onDeleteComment(idComment);
        setLoading(false);
    }

    const handleFileSelect = async (e) => {
        if (!e.target.files) return;

        const files = Array.from(e.target.files);

        for (const file of files) {

            if (file.size > MAX_SIZE) {
                message.error(`❌ File "${file.name}" vượt quá dung lượng tối đa 3MB`);
                continue; // bỏ qua file đó, upload file khác
            }

            const formData = new FormData();
            formData.append("file", file);

            try {
                // call API upload
                const res = await https_taskflow.post("/s3bucketstorage/uploadTemp", formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });

                console.log("res", res);

                // giả sử backend trả về { url: 'https://...' }
                setAttachments(prev => [...prev, res.data.data]);
            } catch (error) {
                console.error("Upload failed", error);
            }
        }

        // reset input để chọn file lần nữa nếu muốn
        e.target.value = "";
    };

    const handleDeleteFileOnUpdate = async (url) => {
        if (!window.confirm("Bạn có muốn xóa ảnh này ?")) return;

        onDeleteCommentAttach(url)
    }



    return (
        <div className="w-full border-t pt-10">

            {loading && (
                <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-50">
                    <SpinnerForSettings />{/* Hoặc component Spinner của bạn */}
                </div>
            )}

            {!loading && <>
                <input
                    type="file"
                    multiple={true}
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
                    className={`space-y-4 mb-4 pr-2 transition-all duration-300 overflow-y-auto ${
                        showComments ? "max-h-80" : "max-h-0"
                    }`}
                >
                    {comments.map((c) => {
                        const isMe = c.accountId === auth.id;

                        return (
                            <div
                                key={c.id}
                                className="flex gap-2 py-2 rounded-lg"
                                onMouseLeave={() => {
                                    setOpenMenu(null);
                                }}
                            >

                                {/* Avatar */}
                                <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold flex-shrink-0">
                                    {c.authorAvatar ? (
                                        <img
                                            src={c.authorAvatar}
                                            alt="avatar"
                                            className="w-full h-full object-cover rounded-full"
                                            onError={(e) => (e.currentTarget.src = '/default-avatar.png')}
                                        />
                                    ) : (
                                        c?.authorName?.[0]?.toUpperCase() || 'U'
                                    )}
                                </div>

                                {/* Comment box */}
                                <div className="flex-1">
                                    {showEditForm !== c.id ? (
                                        <div
                                            className="relative rounded-lg px-5 pb-4 shadow-sm bg-white group"
                                        >
                                            <div className="text-sm font-semibold text-gray-600 mb-1">{c.authorName}</div>
                                            <div className="text-xs text-gray-600 mb-1">{dayjs(c.updatedAt).format(formatToDisplay)}</div>
                                            <div className="text-sm text-gray-700 whitespace-pre-line pt-3">{c.comment}</div>
                                            {!!c.commentAttach?.length && <CommentAttachItem commentAttach={c.commentAttach} />}

                                            {/* Menu button */}
                                            <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                                <button
                                                    onClick={() => setOpenMenu(openMenu === c.id ? null : c.id)}
                                                    className="w-8 h-8 flex items-center justify-center text-gray-600 hover:text-gray-800 bg-gray-100 hover:bg-green-100 rounded-md text-2xl shadow-md focus:outline-none transition-colors duration-200"
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
                                            {!!c.commentAttach?.length && (
                                                <CommentAttachItem
                                                    commentAttach={c.commentAttach}
                                                    onDeleteCommentAttach={onDeleteCommentAttach}
                                                    isEditing
                                                />
                                            )}
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
                        <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold overflow-hidden">
                            {auth.avatar? (
                                <img
                                    src={auth.avatar}
                                    alt="avatar"
                                    className="w-full h-full object-cover"
                                    onError={(e) => (e.currentTarget.src = '/default-avatar.png')}
                                />
                            ) : (
                                auth?.displayName?.[0]?.toUpperCase() || 'U'
                            )}
                        </div>
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
                          <Button onClick={()=> {
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
