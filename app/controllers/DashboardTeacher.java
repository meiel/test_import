package controllers;

import models.*;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by NRAM on 28/04/14.
 */
public class DashboardTeacher extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(
                views.html.Dashboard.Teacher.index.render(
                        // TODO: Página do professor
                        User.findByEmail(request().username()),
                        Discipline.findByUser(request().username()),                // Disciplinas Lecionadas
                        Project.getByTeacherDisciplines(request().username()),      // Todos os projetos das disciplinas lecionadas por um dado professor
                        Milestone.getAllMilestoneByAllDisciplinesTeacher(request().username()), //Todas as etapas de projetos das disciplinas lecionadas por um professor
                        Project.getByTeacher(request().username()),                 // Todos os projetos criados por um professor
                        Milestone.getAllMilestoneByTeacher(request().username()),   // Todas as etapas de projetos criados por um professor
                        StudentMilestone.getLastAvaliations(request().username())
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result disciplines() {
        return ok(
                views.html.Dashboard.Teacher.disciplines.render(
                        User.findByEmail(request().username()),
                        Discipline.findByUser(request().username()), // Disciplinas Lecionadas
                        Project.getByTeacherDisciplines(request().username()), // Todos os projetos das disciplinas lecionadas por um dado professor
                        Milestone.getAllMilestoneByAllDisciplinesTeacher(request().username()), // Todas as milestones dos projetos das disciplinas
                        Milestone.nextDeliveriesMilestoneByAllDisciplinesTeacher(request().username()) // Próximas milestones dos projetos das disciplinas que leciona

                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result showDiscipline(Long id) {
        return ok(
                views.html.Dashboard.Teacher.discipline.render(
                        User.findByEmail(request().username()),
                        Discipline.getById(id),                                 // Informação da Disciplina
                        Project.getAllByDiscipline(id),                         // Todos os projetos da disciplina
                        Milestone.allMilestonesByDiscipline(id),                // Todas as milestones dos projetos de uma disciplina
                        Milestone.nextDeliveryMilestoneByDiscipline(id)         // Próximas milestones a entregar dos projetos de uma disciplina

                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result projects(Long id_discipline) {
        return ok(
                views.html.Dashboard.Teacher.projects.render(
                        User.findByEmail(request().username()),
                        Discipline.getById(id_discipline),
                        Project.getAllByDiscipline(id_discipline),              // Todos os projetos da disciplina
                        Milestone.allMilestonesByDiscipline(id_discipline),      // Todas as milestones dos projetos da disciplina
                        Milestone.nextDeliveryMilestoneByDiscipline(id_discipline)         // Próximas milestones a entregar dos projetos de uma disciplina
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result showProject(Long id_discipline, Long id) {
        return ok(
                views.html.Dashboard.Teacher.project.render(
                        User.findByEmail(request().username()),
                        Project.getById(id),                                    // Vai buscar a informação de um projeto
                        Milestone.allMilestonesByProject(id),                   // Todas as milestones de um projeto
                        Milestone.nextDeliveriesByProject(id)                   // Próximas milestones a entregar de um projeto
                        // TODO: Saber os Grupos e alunos que não entregaram etapas
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result createProject(Long id_discipline) {
        User u = User.findByEmail(request().username());
        List<Discipline> lDiscipline = new ArrayList<>();
        lDiscipline.add(Discipline.getById(id_discipline));
        return ok(
                views.html.Dashboard.Teacher.new_project.render(
                        u,
                        null,
                        lDiscipline,
                        null
                )
        );
    }
    @Security.Authenticated(Secured.class)
    public static Result  updatingMilestone(Long id_project, Long id_milestone){
        User u = User.findByEmail(request().username());
        List<Project> lProjects = new ArrayList<>();
        Milestone milestone = Milestone.getById(id_milestone);
        List<File> files = filesOfMilestone(id_milestone);

        lProjects.add(milestone.project);
        if (lProjects.size() > 0){
            return ok(
                    views.html.Dashboard.Teacher.new_milestone.render(
                            u,
                            milestone,
                            lProjects,
                            files
                    )
            );
        } else {
            return badRequest(
                    views.html.error.render(u,
                            "Não foi encontrada nenhuma disciplina que lecione no corrente ano."
                    )
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updatingProject(Long id){
        User u = User.findByEmail(request().username());
        List<Discipline> lDisciplines = new ArrayList<>();
        Project project = Project.getById(id);
        lDisciplines.add(project.discipline);
        List<File> files = filesOfProject(id);

        if (lDisciplines.size() > 0) {
            return ok(
                    views.html.Dashboard.Teacher.new_project.render(
                            u,
                            project,
                            lDisciplines,
                            files
                    )
            );
        } else {
            return badRequest(
                    views.html.error.render(u,
                            "Não foi encontrada nenhuma disciplina que lecione no corrente ano."
                    )
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateProject(Long id) {
        Project nProject = Projects.updateHandler(id);
        return redirect(routes.Dashboards.showProject(nProject.discipline.id, nProject.id));
    }

    @Security.Authenticated(Secured.class)
    public static Result updateMilestone(Long id) {
        Milestone nMilestone = Milestones.updateHandler(id);
        return redirect(routes.Dashboards.showMilestone(nMilestone.project.id, nMilestone.id));
    }

    @Security.Authenticated(Secured.class)
    public static Result newProject() {
        User u = User.findByEmail(request().username());
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Discipline> lDisciplines = Discipline.allByUserYear(u.getEmail(), year);
        if (lDisciplines.size() > 0) {
            return ok(
                    views.html.Dashboard.Teacher.new_project.render(
                            u,
                            null,
                            lDisciplines,
                            null
                    )
            );
        } else {
            return badRequest(
                    views.html.error.render(u,
                            "Não foi encontrada nenhuma disciplina que lecione no corrente ano."
                    )
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result newMilestoneProject(Long id_discipline, Long id_project) {
        User u = User.findByEmail(request().username());
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Project> lProjects = new ArrayList<>();
        lProjects.add(Project.getById(id_project));
        if (lProjects.size() > 0) {
            return ok(
                    views.html.Dashboard.Teacher.new_milestone.render(
                            u,
                            null,
                            lProjects,
                            null
                    )
            );
        } else {
            return badRequest(
                    views.html.error.render(u,
                            "Não foi encontrada nenhuma disciplina que lecione no corrente ano."
                    )
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result newMilestone() {
        User u = User.findByEmail(request().username());
        int year = Calendar.getInstance().get(Calendar.YEAR);
        List<Project> lProjects = Project.allByUserYear(u.getEmail());//Discipline.allByUserYear(u.getEmail(), year);
        if (lProjects.size() > 0) {
            return ok(
                    views.html.Dashboard.Teacher.new_milestone.render(
                            u,
                            null,
                            lProjects,
                            null
                    )
            );
        } else {
            return badRequest(
                    views.html.error.render(u,
                            "Não foi encontrada nenhuma disciplina que lecione no corrente ano."
                    )
            );
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result saveProject() {
        Project nProject = Projects.saveHandler();
        if (nProject == null) {
            return DashboardTeacher.newProject();
        } else {
            return redirect(routes.Dashboards.showProject(nProject.discipline.id, nProject.id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result saveMilestone() {
        Milestone nMilestone = Milestones.saveHandler();
        if (nMilestone == null) {
            return DashboardTeacher.newMilestone();
        } else {
            return redirect(routes.Dashboards.showMilestone(nMilestone.getProject().discipline.id, nMilestone.id)); //.showProject(nProject.discipline.id, nProject.id));
        }
    }


    @Security.Authenticated(Secured.class)
    public static Result deleteProject(Long id_discipline, Long id) {
        /*
            TODO:
             - Criar Projeto (Não esquecer de selecionar os alunos ou grupos)
         */
        // Não sei como é para fazer neste método
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result milestones(Long id_project) {
        return ok(
                views.html.Dashboard.Teacher.milestones.render(
                        User.findByEmail(request().username()),
                        Project.getById(id_project),                                    // Vai buscar a informação de um projeto
                        Milestone.allMilestonesByProject(id_project),                   // Todas as milestones de um projeto
                        Milestone.nextDeliveriesByProject(id_project)                   // Próximas milestones a entregar de um projeto
                        // TODO: Saber os Grupos e alunos que não entregaram etapas
                        // TODO: Notas Gerais
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result showMilestone(Long id_project, Long id_milestone) {
        /*
            TODO:
                - Informação da Milestone
                - Alunos/Grupos que já entregaram
                - Alunos/Grupos que faltam entregar
         */
        return ok(
                views.html.Dashboard.Teacher.milestone.render(
                        User.findByEmail(request().username()),
                        Project.getById(id_project),                                    // Vai buscar a informação de um projeto
                        Milestone.getById(id_milestone)                                // Informação da milestone
                        // TODO: Saber os Grupos e alunos que não entregaram esta etapa
                        // TODO: Notas Gerais
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result createMilestone(Long id_project) {
        /*
            TODO:
             - Criar nova Milestone
         */
        // Não sei como é para fazer neste método
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteMilestone(Long id_project, Long id_milestone) {
        /*
            TODO:
             - Apagar Milestone
         */
        // Não sei como é para fazer neste método
        return TODO;
    }

    /*@Security.Authenticated(Secured.class)
    public static Result uploadTempFile(){
        String pathFile = Application.uploadToPath("UploadFiles/temp/");
        if (pathFile != null){
            return ok(pathFile);
        } else {
            return badRequest();
        }
    }*/

    @Security.Authenticated(Secured.class)
    public static List<File> filesOfMilestone(Long id_milestone) {
        Milestone m = Milestone.getById(id_milestone);
        List<File> filesToReturn = new ArrayList<File>();

        String path = "Archive/" + m.project.discipline.code + "_" + m.project.discipline.year + "_" + m.project.discipline.id.toString() +
                "/TrabPraticos/" + m.project.code + "_" + m.project.id.toString() + "/" + m.code + "_" + m.id.toString();

        File files = Play.application().getFile(path);

        if (files != null && files.listFiles() != null){
            for(File f : files.listFiles()){
                if(!f.isDirectory()){
                    filesToReturn.add(f);
                }
            }
            return filesToReturn;
        }else{
            return null;
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result downloadFilesOfMilestone(Long id_Project, Long id_Milestone, String filename) {
        Milestone m = Milestone.getById(id_Milestone);

        String path = "Archive/" + m.project.discipline.code + "_" + m.project.discipline.year + "_" + m.project.discipline.id.toString() +
                "/TrabPraticos/" + m.project.code + "_" + m.project.id.toString() + "/" + m.code + "_" + m.id.toString();

        response().setContentType("application/x-download");
        response().setHeader("Content-Disposition", "attachment; filename=" + filename);

        return ok(new java.io.File(path + "/" + filename));
    }

    @Security.Authenticated(Secured.class)
    public static Result uploadFileToProject(Long id_discipline, Long id_project) {
        Project p = Project.getById(id_project);
        String pathResult;

        String pathToSave = "Archive/" + p.discipline.code + "_" + p.discipline.year + "_" + p.discipline.id.toString() +
                            "/TrabPraticos/" + p.code + "_" + p.id.toString();

        pathResult = Application.uploadToPath(pathToSave);

        System.out.println(pathResult);

        if (pathResult == null || pathResult.isEmpty()){
            return null;
        }else{
            return controllers.DashboardTeacher.updatingProject(p.id);
        }
    }

    @Security.Authenticated(Secured.class)
    public static List<File> filesOfProject(Long id_project) {
        Project p = Project.getById(id_project);
        List<File> filesToReturn = new ArrayList<File>();

        String path = "Archive/" + p.discipline.code + "_" + p.discipline.year + "_" + p.discipline.id.toString() +
                      "/TrabPraticos/" + p.code + "_" + p.id.toString();

        File files = Play.application().getFile(path);

        if (files != null && files.listFiles() != null){
            for(File f : files.listFiles()){
                if(!f.isDirectory()){
                    filesToReturn.add(f);
                }
            }
            return filesToReturn;
        }else{
            return null;
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result downloadFilesOfProject(Long id_discipline, Long id_project, String filename) {
        Project p = Project.getById(id_project);

        String path = "Archive/" + p.discipline.code + "_" + p.discipline.year + "_" + p.discipline.id.toString() +
                "/TrabPraticos/" + p.code + "_" + p.id.toString();

        response().setContentType("application/x-download");
        response().setHeader("Content-Disposition", "attachment; filename=" + filename);

        return ok(new java.io.File(path + "/" + filename));
    }

    @Security.Authenticated(Secured.class)
    public static Result uploadFileMilestone(Long id_project, Long id_milestone) {
        Milestone m = Milestone.getById(id_milestone);
        String pathResult;

        String pathToSave = "Archive/" + m.project.discipline.code + "_" + m.project.discipline.year + "_" + m.project.discipline.id.toString() +
                "/TrabPraticos/" + m.project.code + "_" + m.project.id.toString() + "/" + m.code + "_" + m.id.toString();

        pathResult = Application.uploadToPath(pathToSave);

        if (pathResult == null || pathResult.isEmpty()){
            return null;
        }else{
            return controllers.DashboardTeacher.updatingMilestone(id_project, id_milestone);
        }
    }


    @Security.Authenticated(Secured.class)
    public static Result avaliations(Long id_project, Long id_milestone) {
        /*
            TODO:
                - Mostrar notas da milestone por grupo e aluno
                - Mostrar as datas das entregas

         */
        return ok(
                views.html.Dashboard.Teacher.avaliations.render(
                        User.findByEmail(request().username()),
                        Project.getById(id_project),                                    // Vai buscar a informação de um projeto
                        Milestone.getById(id_milestone),                                // Informação da milestone
                        StudentMilestone.getByMilestone(id_milestone)
                )
        );
    }

    @Security.Authenticated(Secured.class)
    public static Result showAvaliation(Long id_project, Long id_milestone) {
        /*
            TODO:
                - Mostrar informação da avaliação
         */
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result createAvaliation(Long id_project, Long id_milestone) {
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteAvaliation(Long id_project, Long id_milestone) {
        return TODO;
    }

    @Security.Authenticated(Secured.class)
    public static Result showAvaliationByGroup(Long id_project, Long id_milestone, Long id_group) {
        // Verificar se pertence à milestone
        GroupMilestone gm = GroupMilestone.getByGroupMilestone(id_milestone, id_group);
        User u = User.findByEmail(request().username());
        if (gm != null) {
            // TODO: Se calhar é melhor criar outra VIEW
            return ok(
                    views.html.Dashboard.Teacher.avaliation.render(
                            u,
                            gm,
                            null
                    )
            );
        }

        // TODO: Não sei se é a melhor maneira de tratar este assunto. (Quando não pertence à milestone)
        return badRequest(views.html.notFound.render(u));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAvaliationByStudent(Long id_project, Long id_milestone, Long id_student) {
        // Verificar se pertence à milestone
        StudentMilestone sm = StudentMilestone.getByStudentMilestone(id_milestone, id_student);
        User u = User.findByEmail(request().username());
        if (sm != null) {
            // TODO: Se calhar é melhor criar outra VIEW
            return ok(
                    views.html.Dashboard.Teacher.avaliation.render(
                            u,
                            null,
                            sm
                    )
            );
        }

        // TODO: Não sei se é a melhor maneira de tratar este assunto. (Quando não pertence à milestone)
        return badRequest(views.html.notFound.render(u));
    }
}
