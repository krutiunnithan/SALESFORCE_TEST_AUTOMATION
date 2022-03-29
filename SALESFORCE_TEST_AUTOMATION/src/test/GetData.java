package test;
import org.testng.annotations.DataProvider;

public class GetData {
        @DataProvider(name = "caseCreation")
        public static Object[][] caseCreationfieldsDataset() {
            return new Object[][] {{ "Phone", "Password Request" },
                                   { "--None--", "   " },
                                   { "Phone", " P@ssword Request" },
                                   { "Email", "Password Request" },
                                   { "Phone", ""},
                                   { "Email", "#@*0!" },
                                   { "Web", "Password Request " },
                                   { "Phone", "PasswordRequest" },
                                   { "Phone", "Password request" },
                                   { "Phone", "password Request" },
                                   { "Phone", "password request" },
            };
        }

        @DataProvider(name = "caseCreationEdit")
        public static Object[][] caseCreationEditDataset() {
            return new Object[][] {{ "Email", "Email Issue Request", "Phone", "Password Request" },
                                   { "Phone", "   ", "Phone", "Password Request" },
                                   {"Phone", "Password Request", "Phone", "Password Request"},
            };
        }

        @DataProvider(name = "caseDefaultAssignment")
        public static Object[][] defaultSubfieldsDataset() {
            return new Object[][] {{ "Phone", "" },
                                   { "Phone", "   " },
                                   { "Phone", "subject2" },
                                   { "Email", ""},
                                   { "Email", "subject4" },
                                   { "Email", "#@*0!" },
                                   { "Phone", " subj6 " }
                                  };
        }

        @DataProvider(name = "caseDefaultAssignmentEdit")
        public static Object[][] defaultSubfieldsEditDataset() {
            return new Object[][] {{ "Phone", "", "Phone", "" },
                                   { "Phone", "", "phone", "" },
                                   { "Phone", "Password Request", "Phone", "   " },
                                   { "Phone", "", "Phone", "check" },
                                   { "Email", "", "Phone", ""}
            };
        }
}

