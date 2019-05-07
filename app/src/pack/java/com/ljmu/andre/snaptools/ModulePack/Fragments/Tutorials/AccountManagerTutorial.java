package com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;
import com.ljmu.andre.snaptools.Utils.TutorialDetail.MessagePosition;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AccountManagerTutorial {
    public static ImmutableList<TutorialDetail> getTutorials() {
        return new ImmutableList.Builder<TutorialDetail>()

                // ===========================================================================

                .add(
                        new TutorialDetail()
                                .setMessagePosition(MessagePosition.MIDDLE)
                                .setTitle("Encrypted Accounts")
                                .setMessage(
                                        "The SnapTools Account Manager fully encrypts your accounts so that only YOU have access to them."
                                                + "\nAs a result of this you must enter your password to decrypt your account files whenever you wish to access them."
                                                + "\nOnce you first assign your password it is impossible to retrieve your accounts without it."
                                )
                )

                .add(
                        new TutorialDetail()
                                .setMessagePosition(MessagePosition.MIDDLE)
                                .setTitle("Root Warning")
                                .setMessage(
                                        "This feature is mostly aimed at Magisk users as it requires root access to fetch your account data from within Snapchat."
                                                + "\nThis dramatically slows down the backup process for system root users as it requires you to Unroot->Login to Snapchat->Re root->Backup Account->Repeat."
                                                + "\nWhereas Magisk users just require xposed to be disabled"
                                )
                )

                .add(
                        new TutorialDetail()
                                .setMessagePosition(MessagePosition.MIDDLE)
                                .setTitle("Backup a single account")
                                .setMessage(
                                        "To backup a single account it's quite simple."
                                                + "\n1) Ensure your Account Manager is unlocked and you have provided a password."
                                                + "\n2) Navigate to the Account Manager Settings and tap the Backup Current Account button."
                                                + "\n3) Provide an identifier for the account (Generally the account username, it will be encrypted)."
                                                + "\n4) Wait for the accounts to be backed up and encrypted."
                                )
                )
                .add(
                        new TutorialDetail()
                                .setMessagePosition(MessagePosition.MIDDLE)
                                .setTitle("Backing up multiple accounts")
                                .setMessage(
                                        "To backup multiple accounts you must ENSURE Xposed is disabled."
                                                + "\n1) Backup your current account as detailed in the previous menu."
                                                + "\n2) [IMPORTANT] Nagivate to the Account Manager Settings and tap the Safe Logout button. This will ensure your previous authentication token remains valid."
                                                + "\n3) Log into Snapchat using your next account."
                                                + "\n4) Repeat (Step 1) for as many accounts as needed"
                                )
                )

                // ===========================================================================

                .build();
    }
}
