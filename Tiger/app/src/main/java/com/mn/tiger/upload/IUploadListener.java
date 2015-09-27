package com.mn.tiger.upload;


public interface IUploadListener
{	
	void uploadStart(TGUploader uploader);
	
    void uploadSucceed(TGUploader uploader);
	
	void uploadFailed(TGUploader uploader);
	
	void uploadProgress(TGUploader uploader, int progress);
	
	void uploadCanceled(TGUploader uploader);
	
	void uploadStop(TGUploader downloader);
}
