import {DeleteOutlined} from "@ant-design/icons";
import {FileIcon} from "./FileIcon";


export function CommentAttachItemAdd({attachments, setAttachments}) {

    const isImage = (url) => {
        return /\.(png|jpg|jpeg|gif|webp|svg)$/i.test(url);
    };

    return (
        <div className="flex flex-wrap gap-2 mt-2 w-full">
            {attachments.map((url, idx) => {
                let filename = url.split('/').pop();

                filename = filename.replace(/^[0-9a-fA-F-]{36}-/, '');

                return (
                    <div
                        key={idx}
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
                );
            })}
        </div>
    )
}