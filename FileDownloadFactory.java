@Component
public class FileDownloadFactory {
	
	@Autowired
	ComplaintsFileDownload complaintsFileDownload;
	
	public FileDownload getFileDownload(String type) {
		switch (type) {
		case "ComplainceBookReport":
			return complaintsFileDownload;
		
		}
		return null;
	}
}
