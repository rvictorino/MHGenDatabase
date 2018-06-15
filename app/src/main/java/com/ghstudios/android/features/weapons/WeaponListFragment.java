package com.ghstudios.android.features.weapons;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ghstudios.android.RecyclerViewFragment;
import com.ghstudios.android.adapter.WeaponExpandableListBladeAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowgunAdapter;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.adapter.WeaponExpandableListGeneralAdapter;

/**
 * The weapon list fragment for every weapon type.
 */
public class WeaponListFragment extends RecyclerViewFragment {

    protected static final String ARG_TYPE = "WEAPON_TYPE";

    private WeaponListViewModel viewModel;
    private WeaponExpandableListGeneralAdapter mAdapter;

    public static WeaponListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(WeaponListFragment.ARG_TYPE, type);
        WeaponListFragment f = new WeaponListFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(WeaponListViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            String weaponType = args.getString(ARG_TYPE);
            viewModel.loadWeaponType(weaponType);
        }
    }

    /**
     * Used by onCreateView to build an adapter that creates the actual views for all weapons.
     * @return
     */
    protected WeaponExpandableListGeneralAdapter createWeaponAdapter() {
        String weaponType = viewModel.getWeaponType();
        if (weaponType == null || weaponType.equals("")) {
            return null;
        }

        View.OnLongClickListener listener = (v) -> {
            collapseItem(v);
            return true;
        };

        if (weaponType.equals(Weapon.BOW)) {
            return new WeaponExpandableListBowAdapter(getActivity(), listener);
        }

        if (weaponType.equals(Weapon.HEAVY_BOWGUN) || weaponType.equals(Weapon.LIGHT_BOWGUN)) {
            return new WeaponExpandableListBowgunAdapter(getActivity(), listener);
        }

        return new WeaponExpandableListBladeAdapter(getActivity(), listener);
    }

    /**
     * Collapses an item in the list using its view
     * @param v
     */
    protected void collapseItem(View v) {
        if (mAdapter != null) {
            int position = this.getRecyclerView().getChildAdapterPosition(v);
            mAdapter.collapseGroup(position);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = createWeaponAdapter();
        this.setAdapter(mAdapter);

        viewModel.getWeaponListData().observe(this, (weapons) -> {
            mAdapter.clear();
            mAdapter.addAll(weapons);
        });

        // restore collapsed groups if we are restoring the fragment
        if (viewModel.getCollapsedGroups() != null) {
            mAdapter.restoreGroups(viewModel.getCollapsedGroups());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_weapon, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_final:
                item.setChecked(!item.isChecked());
                viewModel.setFilterFinal(item.isChecked());
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            viewModel.setCollapsedGroups(mAdapter.saveGroups());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            viewModel.setCollapsedGroups(mAdapter.saveGroups());
        }
    }
}
