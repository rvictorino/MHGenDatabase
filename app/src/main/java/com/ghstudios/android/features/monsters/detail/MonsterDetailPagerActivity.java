package com.ghstudios.android.features.monsters.detail;

import android.arch.lifecycle.ViewModelProviders;

import com.ghstudios.android.data.classes.meta.MonsterMetadata;
import com.ghstudios.android.loader.HuntingRewardListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.mhgendatabase.R;

public class MonsterDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_MONSTER_ID =
            "com.daviancorp.android.android.ui.detail.monster_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long monsterId = getIntent().getLongExtra(EXTRA_MONSTER_ID, -1);

        MonsterDetailViewModel viewModel = ViewModelProviders.of(this).get(MonsterDetailViewModel.class);
        MonsterMetadata meta = viewModel.setMonster(monsterId);

        setTitle(meta.getName());

        tabs.addTab(getString(R.string.monster_detail_tab_summary), () ->
                MonsterSummaryFragment.newInstance(monsterId)
        );

        if (meta.getHasDamageData() || meta.getHasStatusData()) {
            // only include Damage tab if there is data
            tabs.addTab(getString(R.string.monster_detail_tab_damage), () ->
                    MonsterDamageFragment.newInstance(monsterId)
            );
        }

        if(meta.getHasLowRank()) {
            tabs.addTab(getString(R.string.monster_detail_tab_lr), () ->
                    MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_LR)
            );
        }

        if(meta.getHasHighRank()) {
            tabs.addTab(getString(R.string.monster_detail_tab_hr), () ->
                    MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_HR)
            );
        }

        if(meta.getHasGRank()) {
            tabs.addTab(getString(R.string.monster_detail_tab_g), () ->
                    MonsterRewardFragment.Companion.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_G)
            );
        }

        tabs.addTab(getString(R.string.monster_detail_tab_quest), () ->
                MonsterQuestFragment.newInstance(monsterId)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.MONSTERS;
    }
}
