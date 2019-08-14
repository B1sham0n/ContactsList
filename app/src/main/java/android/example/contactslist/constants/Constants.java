package android.example.contactslist.constants;

public final class Constants {
    public static final class Names{
        private static final String FAVORITE_DB = "favorite";
        private static final String CONTACTS_DB = "peoples";

        public static String getFavoriteDB() {
            return FAVORITE_DB;
        }

        public static String getContactsDB() {
            return CONTACTS_DB;
        }
    }
    public static final class URI{
        private static final String COMMON_PART = "content://com.android.contacts/contacts/";

        public static String getCommonPart() {
            return COMMON_PART;
        }
    }
}
