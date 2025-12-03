import {DeleteOutlined} from "@ant-design/icons";
import {https_taskflow} from "../../service/api";
import {FaDownload, FaFileAlt, FaFileExcel, FaFilePdf, FaFilePowerpoint, FaFileWord} from "react-icons/fa";
import {FileIcon} from "./FileIcon";


export function CommentAttachItem({commentAttach, isEditing, onDeleteCommentAttach}) {

    const isImage = (url) => {
        return /\.(png|jpg|jpeg|gif|webp|svg)$/i.test(url);
    };



    const handleDownload = async (url) => {
        try {
            const response = await https_taskflow.get("/s3bucketstorage/download",{
                params: {
                    url: url
                }
            });
            if (!response.ok) throw new Error("Không tải được file");

            // Nhận dữ liệu dạng file (blob)
            const blob = await response.blob();

            // Tạo URL tạm
            const blobUrl = window.URL.createObjectURL(blob);

            // Tách tên file từ url BE (nếu có)
            const filename = url.split("/").pop() || "downloaded_file";

            // Tạo thẻ <a> để trigger download
            const link = document.createElement("a");
            link.href = blobUrl;
            link.download = filename;
            document.body.appendChild(link);
            link.click();

            // Dọn rác
            link.remove();
            window.URL.revokeObjectURL(blobUrl);
        } catch (err) {
            console.error("Download lỗi:", err);
        }
    };


    return (
        <div className="flex flex-wrap gap-2 mt-2 w-full">
            {commentAttach.map(att => {
                const url = att.attachmentUrl;
                // 1. Lấy phần cuối của URL
                let filename = url.split('/').pop();

                filename = filename.replace(/^[0-9a-fA-F-]{36}-/, '');

                return (
                    <div
                        key={att.id}
                        className="w-28 h-28 relative border rounded overflow-hidden bg-gray-50 flex items-center justify-center"
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
                            <div className="flex flex-col items-center justify-center p-1 text-center">
                                <FileIcon type={filename.split('.').pop()} /> {/* component hiển thị icon theo type */}
                                <span className="text-xs truncate w-16">{filename}</span>
                            </div>
                        )}
                        {/* Nút Download */}
                        {!isEditing && (
                            <button
                                // onClick={()=>handleDownload(url)}
                                className="absolute top-1 right-1 bg-white bg-opacity-80 px-1 py-0.5 text-[10px] rounded border hover:bg-blue-100 flex items-center gap-1"
                            >
                                <FaDownload />
                            </button>
                        )}

                        {isEditing && <button
                            onClick={() =>
                                onDeleteCommentAttach(url)
                            }
                            className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600"
                        >
                            <DeleteOutlined style={{ fontSize: '14px' }} />
                        </button>

                        }
                    </div>
                );
            })}
        </div>
    )
}