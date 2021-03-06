package business.custom;

import business.SuperBO;
import util.AdminTM;
import util.CourseTM;
import util.StudentTM;

import java.sql.SQLException;
import java.util.List;

public interface StudentBO extends SuperBO {
    String getNewStudentId() throws Exception;
    List<StudentTM> getAllStudents() throws Exception;
    boolean saveStudent(String id,String facultyId, String name,String address, String contact,String username, String password,String nic, String email) throws Exception;
    boolean deleteStudent(String id)throws Exception;
    boolean updateStudent(String facultyId, String name,String address, String contact,String username, String password,String nic, String email,String id)throws Exception;
    List<CourseTM> getStudentCourses(String studentId) throws Exception;
}
