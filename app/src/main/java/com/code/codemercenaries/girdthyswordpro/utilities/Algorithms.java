package com.code.codemercenaries.girdthyswordpro.utilities;

import com.code.codemercenaries.girdthyswordpro.beans.remote.User;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 09-02-2019.
 */

public class Algorithms {

    public int searchUser(ArrayList<User> users, String uuid) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }
}
