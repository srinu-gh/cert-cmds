@Service
public class ComplaintService {

	public byte[] downloadTotalReceivedBooksReport(FileDownload fd) {
		
		ArrayList<Book> books = new ArrayList<>(List.of(
                new Book(
                        "Java in action",
                        "mary public",
                        "Everest publishers",
                        LocalDate.of(2021, Month.JANUARY, 8),
                        Double.valueOf(5.1)),
                new Book(
                        "Introduction to Java",
                        "mary public",
                        "Heavyweight publishers",
                        LocalDate.of(2022, Month.JANUARY, 9),
                        Double.valueOf(5.2)),
                new Book(
                        "Advanced databases",
                        "charles darwin",
                        "Longhorn publishers",
                        LocalDate.of(2023, Month.JANUARY, 10),
                        Double.valueOf(5.3))));
		
        return fd.getFile(books);
//        return fd.getFile(complaintRepository.downloadTotalReceivedDisposedPendingByPeriodByPeriodReport(criteria));
    }
	
}
