package com.corelabsplus.getmethere.utils;

/**
 * Created by Victor.
 */

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.corelabsplus.getmethere.R;
import com.corelabsplus.getmethere.activities.MapsActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DrawerUtil {

    public static void getDrawer(final Activity activity, Toolbar toolbar){

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.colorAccent)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz")
                                .withEmail("mikepenz@gmail.com")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

//        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
//        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemManageProfile = new PrimaryDrawerItem().withIdentifier(1)
                .withName("Profile");
                //.withIcon(R.drawable.ic_account);

        PrimaryDrawerItem drawerItemAbout = new PrimaryDrawerItem().withIdentifier(3)
                .withName("About");
                //.withIcon(R.drawable.ic_info);
        PrimaryDrawerItem drawerItemHelp = new PrimaryDrawerItem().withIdentifier(4)
                .withName("Help");
                //.withIcon(R.drawable.ic_help);
        SecondaryDrawerItem drawerItemLogout = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Logout");
                //.withIcon(R.drawable.ic_exit);

        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withAccountHeader(headerResult)
                .withSelectedItem(-1)
                .addDrawerItems(
//                        drawerEmptyItem,drawerEmptyItem,drawerEmptyItem,
                        drawerItemManageProfile,
                        drawerItemAbout,
                        drawerItemHelp,
                        new DividerDrawerItem(),
                        drawerItemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 2 && !(activity instanceof MapsActivity)) {
                            // load tournament screen
//                            Intent intent = new Intent(activity, MapsActivity.class);
//                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }
}
