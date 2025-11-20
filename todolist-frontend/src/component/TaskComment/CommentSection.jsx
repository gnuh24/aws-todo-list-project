import { useState, useEffect, useRef } from "react";
import {Input, Button, message} from "antd";
import {
    PaperClipOutlined,
    UpOutlined,
    DeleteOutlined
} from "@ant-design/icons";
import dayjs from "dayjs";
import {https_taskflow} from "../../service/api";

const MAX_SIZE = 3 * 1024 * 1024;

export default function CommentSection({ isOpenComment, comments, handleComment, onUpdateComment, onDeleteComment, onDeleteCommentAttach }) {
    const [newComment, setNewComment] = useState("");
    const [attachments, setAttachments] = useState([]); // list URL trả về từ backend
    const [showComments, setShowComments] = useState(isOpenComment ?? false);
    const [showOption, setShowOption] = useState(null);
    const [showEditForm, setShowEditForm] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);
    const [openMenu, setOpenMenu] = useState(null);
    const [auth] = useState(() => {
        const raw = localStorage.getItem("USER_INFO");
        if (!raw) return null;
        try { return JSON.parse(raw); } catch { return null; }
    });
    const formatToDisplay = "HH:mm DD/MM"
    const containerRef = useRef(null);
    const menuRef = useRef(null);
    const fileInputRef = useRef(null);

    const isImage = (url) => {
        return /\.(png|jpg|jpeg|gif|webp|svg)$/i.test(url);
    };


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
        if (!newComment.trim()) {
            alert("Không bỏ trống nội dung comment")
            return
        }
        await handleComment(newComment, attachments.length > 0 ? attachments : null);
        setAttachments([]);
        setNewComment("");
    };

    const handleUpdate = async (idComment) => {
        if (!newComment.trim()) {
            alert("Không bỏ trống nội dung comment")
            return
        }
        await onUpdateComment(newComment, idComment);
        setNewComment("");
        setShowEditForm(null);
    };

    const handleDelete = async (idComment) => {
        if (!window.confirm("Bạn có muốn xóa comment này không?")) return
        await onDeleteComment(idComment);
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
                {comments.map((c, i) => {
                    const isMe = c.accountId === auth.id;
                    return (
                        <div
                            key={i}
                            className={`flex gap-2 py-2 px-2 rounded-lg ${
                                isMe ? 'flex-row-reverse text-right' : 'flex-row text-left'
                            }`}
                            onMouseEnter={() => {
                                if (showEditForm !== c.id) setShowOption(c.id)
                            }}
                            onMouseLeave={() => {
                                setShowOption(null)
                                setOpenMenu(null)
                            }}
                        >
                            {/* Avatar */}
                            <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold">
                                {c.authorAvatar? (
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
                            {showEditForm !== c.id && (<div className={`border rounded-lg px-5 py-4 shadow-sm max-w-xs bg-white ${
                                isMe ? 'border-green-400' : 'border-gray-300'
                            } relative`}>
                                <div className="text-sm font-semibold text-gray-600 mb-1">
                                    <div className="text-sm font-semibold text-gray-600 mb-1">
                                        {c.authorName}
                                    </div>
                                    <div className="text-xs text-gray-600 mb-1">
                                        {dayjs(c.updatedAt).format(formatToDisplay)}
                                    </div>

                                </div>

                                <div className="text-sm text-gray-700 whitespace-pre-line pt-3">{c.comment}</div>

                                {/* Comment attachments */}
                                {!!c.commentAttach?.length && (
                                    <div className="flex flex-wrap gap-2 mt-2 w-full">
                                        {c.commentAttach.map(att => {
                                            const url = att.attachmentUrl;
                                            // 1. Lấy phần cuối của URL
                                            let filename = url.split('/').pop();

                                            filename.replace(/^[0-9a-fA-F\-]{36}-/, '');

                                            return (
                                                <div
                                                    key={att.id}
                                                    className="w-20 h-20 relative border rounded overflow-hidden bg-gray-50 flex items-center justify-center"
                                                >
                                                    {isImage(url) ? (
                                                        <img
                                                            src={url}
                                                            alt=""
                                                            className="w-full h-full object-cover"
                                                            onError={(e) => {
                                                                if (e.currentTarget.src !== '/file-broken.png') {
                                                                    e.currentTarget.src = '/file-broken.png';
                                                                }
                                                            }}
                                                        />
                                                    ) : (
                                                        <a
                                                            href={url}
                                                            target="_blank"
                                                            rel="noreferrer"
                                                            className="text-xs text-blue-600 underline p-1 break-words text-center"
                                                        >
                                                            {filename}
                                                        </a>
                                                    )}
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>)}




                            {showEditForm === c.id && (<div className="w-full border rounded-md p-3">
                                <Input.TextArea
                                    rows={3}
                                    defaultValue={c.comment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                    className="border-none focus:ring-0 resize-none"
                                    autoFocus
                                />
                                    {/* Comment attachments */}
                                    {!!c.commentAttach?.length && (
                                        <div className="flex flex-wrap gap-2 mt-2 w-full">
                                            {c.commentAttach.map(att => {
                                                const url = att.attachmentUrl;
                                                // 1. Lấy phần cuối của URL
                                                let filename = url.split('/').pop();

                                                filename.replace(/^[0-9a-fA-F\-]{36}-/, '');

                                                return (
                                                    <div
                                                        key={att.id}
                                                        className="w-20 h-20 relative border rounded overflow-hidden bg-gray-50 flex items-center justify-center"
                                                    >
                                                        {isImage(url) ? (
                                                            <img
                                                                src={url}
                                                                alt=""
                                                                className="w-full h-full object-cover"
                                                                onError={(e) => {
                                                                    if (e.currentTarget.src !== '/file-broken.png') {
                                                                        e.currentTarget.src = '/file-broken.png';
                                                                    }
                                                                }}
                                                            />
                                                        ) : (
                                                            <a
                                                                href={url}
                                                                target="_blank"
                                                                rel="noreferrer"
                                                                className="text-xs text-blue-600 underline p-1 break-words text-center"
                                                            >
                                                                {filename}
                                                            </a>
                                                        )}
                                                        {/* Nút xóa */}
                                                        <button
                                                            onClick={() =>
                                                                handleDeleteFileOnUpdate(url)
                                                            }
                                                            className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600"
                                                        >
                                                            <DeleteOutlined style={{ fontSize: '14px' }} />
                                                        </button>
                                                    </div>

                                                );
                                            })}
                                        </div>
                                )}
                                <div className="flex justify-between items-center mt-2">
                                    <div className="flex gap-3 text-gray-400 text-lg">
                                    </div>
                                    <div className="flex gap-2">
                                        <Button onClick={()=> setShowEditForm(null)}>Cancel</Button>
                                        <Button type="primary" danger onClick={() => handleUpdate(c.id)}>
                                            Update
                                        </Button>
                                    </div>
                                </div>
                            </div>)}

                            {/* 3 chấm ngoài comment box */}
                            {isMe && showOption === c.id && (
                                <div className="relative flex items-end" ref={menuRef}>
                                    <button
                                        onClick={() => {
                                            if (openMenu === null) {
                                                setOpenMenu(c.id)
                                            }else{
                                                setOpenMenu(null)
                                            }
                                        }}

                                        className="w-5 h-5 flex items-center justify-center text-gray-500 hover:text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-full transition-colors duration-200 shadow-sm focus:outline-none"
                                    >
                                        ⋮
                                    </button>

                                    {openMenu === c.id && (
                                        <div className="absolute bottom-5 mb-2 right-0 w-24 bg-white border rounded shadow-md z-10">
                                            <button className="w-full text-left px-2 py-1 hover:bg-gray-100 text-sm" onClick={() => {
                                                setShowEditForm(c.id)
                                                setShowOption(null)
                                                setIsExpanded(false)
                                                setNewComment("") // Reset lại biến newComment trong trường hợp đang tạo mới mà cancel
                                            }}>Edit</button>
                                            <button className="w-full text-left px-2 py-1 hover:bg-gray-100 text-sm text-red-500" onClick={
                                                // Đóng form edit và add nếu đang mở
                                                () => {
                                                    setShowEditForm(null)
                                                    setIsExpanded(false)
                                                    setNewComment("")
                                                    handleDelete(c.id)
                                                    setShowOption(null) // Đóng form chọn
                                                }
                                            }>Delete</button>
                                        </div>
                                    )}
                                </div>

                            )}
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
                        <div className="flex flex-wrap gap-2 mt-2">
                            {attachments.map((url, idx) => (
                                <div key={idx} className="w-20 h-20 relative border rounded overflow-hidden bg-gray-50 flex items-center justify-center">
                                    {isImage(url) ? (
                                        <img src={url} className="w-full h-full object-cover" alt=""/>
                                    ) : (
                                        <a href={url} target="_blank" rel="noreferrer" className="text-xs text-blue-600 underline p-1 break-words text-center">
                                            File
                                        </a>
                                    )}
                                    {/* Nút xóa */}
                                    <button
                                        onClick={() =>
                                            setAttachments(prev => prev.filter((_, i) => i !== idx))
                                        }
                                        className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600"
                                    >
                                        <DeleteOutlined style={{ fontSize: '14px' }} />
                                    </button>
                                </div>
                            ))}
                        </div>
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
        </div>
    );
}
