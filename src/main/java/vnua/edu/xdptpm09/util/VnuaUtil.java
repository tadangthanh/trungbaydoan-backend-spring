package vnua.edu.xdptpm09.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.UserVNUADTO;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;

@Component
public class VnuaUtil {
    @Value("${schedule.base.api}")
    private String baseApi;

    public UserVNUADTO getInfoUserFromVNUA(String code) {
        try {
            UserVNUADTO userVNUADTO = new UserVNUADTO();
            String url = this.baseApi + "?page=thoikhoabieu&sta=1&id=" + code;
            Document doc = Jsoup.connect(url)
                    .header("Accept", "text/html")
                    .get();

            Element elementCapcha = doc.getElementById("ctl00_ContentPlaceHolder1_ctl00_lblCapcha");
            if(elementCapcha != null) {
                System.out.println("Capcha: " + elementCapcha.text());
                return null;
            }
            Element elementHoTen = doc.getElementById("ctl00_ContentPlaceHolder1_ctl00_lblContentTenSV");
            Element elementClass = doc.getElementById("ctl00_ContentPlaceHolder1_ctl00_lblContentLopSV");
            if (elementHoTen != null && elementClass != null) {
                String fullName = elementHoTen.text().split("-")[0].trim();
                String classText = elementClass.text();
                userVNUADTO.setFullName(fullName);
                if (this.isEmptyString(classText)) {
                    return userVNUADTO;
                } else {
                    String[] arr = classText.split("-");
                    int academicYear = 0;
                    String major = "";
                    String department = "";
                    String className = "";
                    if (arr.length == 3) {
                        className = arr[0].trim();
                        major = arr[2].split(":")[1].trim();
                        department = arr[1].split(":")[1].trim();
                        String academicYearStr = this.getAcademicYear(arr[0].trim());
                        if (this.isNumeric(academicYearStr)) {
                            academicYear = Integer.parseInt(academicYearStr);
                        }
                    }
                    userVNUADTO.setMajor(major);
                    userVNUADTO.setClassName(className);
                    userVNUADTO.setDepartment(department);
                    userVNUADTO.setAcademicYear(academicYear);
                    return userVNUADTO;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
           throw new ResourceNotFoundException("Không tìm thấy thông tin người dùng");
        }
    }

    public boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String getAcademicYear(String input) {
        int startIndex = input.indexOf(75) + 1;
        StringBuilder sb = new StringBuilder();

        for(int i = startIndex; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if (!Character.isDigit(ch)) {
                break;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

}
