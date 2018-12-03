class Constants {
    static final String[] GRAPHS = new String[]{
            "20170830",
            "20170907",
            "20170913",
            "20170920",
            "20170927"
    };

    static final String DATA_FOLDER = "/home/icuevas/data/";

    static int getIdNumber(String entity) {
        assert entity.startsWith("<") && entity.endsWith(">");
        int index = entity.lastIndexOf("/Q");
        if (index < 0)
            return index;
        entity = entity.substring(index + 2, entity.length() - 1);
        int number;
        try {
            number = Integer.parseInt(entity);
        }
        catch (NumberFormatException e) {
            number = -1;
        }
        return number;
    }
}
