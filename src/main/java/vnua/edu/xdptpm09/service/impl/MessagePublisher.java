//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.entity.Notification;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.repository.NotificationRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.Observer;
import vnua.edu.xdptpm09.service.Subject;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagePublisher implements Subject {
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;
    List<Observer> observers = new ArrayList<>();

    private void getStudents() {
        List<Observer> list = new ArrayList<>();
        for (User user : this.userRepo.findStudents()) {
            Observer observer = (Observer) user;
            list.add(observer);
        }
        this.observers = list;
    }

    private void getTeachers() {
        List<Observer> list = new ArrayList<>();
        for (User user : this.userRepo.findTeachers()) {
            Observer observer = (Observer) user;
            list.add(observer);
        }
        this.observers = list;
    }

    public void sendToTeacher(Notification notification) {
        this.getTeachers();
        for (Observer observer : this.observers) {
            observer.update(notification);
            notification.setReceiver((User) observer);
        }

        this.notificationRepo.saveAndFlush(notification);
    }

    public void sendToStudent(Notification notification) {
        this.getStudents();

        for (Observer observer : this.observers) {
            observer.update(notification);
            notification.setReceiver((User) observer);
        }

        this.notificationRepo.saveAndFlush(notification);
    }


}
