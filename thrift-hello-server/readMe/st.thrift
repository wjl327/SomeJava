namespace java com.service.st

struct Student
{
	1:i32 id,
	2:string name
}

service StudentService{
	bool addStudent(1:Student student);
}

struct Teacher
{
    1:i32 id,
	2:string name,
	3:bool master
}

service TeacherService{
	Teacher getTeacher(1:i32 id);
}
