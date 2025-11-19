package Interfaces;

public interface OrganizerInterface {
    void uploadCSV();
    void formTeams();
    void viewResults();
    void searchMember(String query);
    void searchTeam(String teamId);
}

//public interface OrganizerInterface {
//    void uploadCSV();
//    void formTeams();
//    void viewResults();
//    void searchMember(String query);
//}
//
//// interfaces/OrganizerInterface.java
//public interface OrganizerInterface {
//    void uploadCSV();      // choose existing or new CSV
//    void formTeams();      // run team-formation logic
//    void viewResults();    // display / export results
//}