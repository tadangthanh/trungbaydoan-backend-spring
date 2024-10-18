
package vnua.edu.xdptpm09.service;

import vnua.edu.xdptpm09.entity.Notification;

public interface Subject {
    void sendToTeacher(Notification notification);

    void sendToStudent(Notification notification);
}
