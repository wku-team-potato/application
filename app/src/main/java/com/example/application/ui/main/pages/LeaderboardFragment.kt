package com.example.application.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.FragmentLeaderboardBinding
import com.example.application.ui.leaderboard.function.repository.LeaderBoardRepository
import com.example.application.ui.leaderboard.function.service.LeaderBoardService
import com.example.application.ui.leaderboard.function.viewmodel.LeaderBoardViewModel
import com.example.application.ui.leaderboard.function.viewmodel.LeaderBoardViewModelFactory
import com.example.application.ui.meals.CalorieActivity

class LeaderboardFragment : BaseFragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderBoardViewModel: LeaderBoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        loadRankings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initViewModel()
        observeViewModel()
        loadRankings()
    }

    private fun initUi() = with(binding) {
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
                showMyPage()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initViewModel() {
        val repository = LeaderBoardRepository(RetrofitInstance.leaderBoardService)
        val factory = LeaderBoardViewModelFactory(repository)
        leaderBoardViewModel = ViewModelProvider(this, factory).get(LeaderBoardViewModel::class.java)
    }

    private fun observeViewModel() = with(binding) {
        // 마이 랭킹
        leaderBoardViewModel.myRanking.observe(viewLifecycleOwner) { ranking ->
            ranking?.let {
                myName.text = it.user_info.username
                myRankingAttend.text = "${it.rankings.consecutive_attendance_rank}위"
                myRankingGoals.text = "${it.rankings.consecutive_goals_rank}위"
            }
        }

        // Top3 랭킹
        leaderBoardViewModel.topRankings.observe(viewLifecycleOwner) { topRanking ->
            topRanking?.let {
                // 연속 출석
                if (it.consecutive_attendance_rank.size >= 3) {
                    attendFirstRank.text = "${topRanking.consecutive_attendance_rank[0].rank}위"
                    attendFirstName.text = it.consecutive_attendance_rank[0].username
                    attendFirstData.text = "${it.consecutive_attendance_rank[0].consecutiveAttendanceDays}일 연속"

                    attendSecondRank.text = "${topRanking.consecutive_attendance_rank[1].rank}위"
                    attendSecondName.text = it.consecutive_attendance_rank[1].username
                    attendSecondData.text = "${it.consecutive_attendance_rank[1].consecutiveAttendanceDays}일 연속"

                    attendThirdRank.text = "${topRanking.consecutive_attendance_rank[2].rank}위"
                    attendThirdName.text = it.consecutive_attendance_rank[2].username
                    attendThirdData.text = "${it.consecutive_attendance_rank[2].consecutiveAttendanceDays}일 연속"
                }

                // 연속 목표 달성
                if (it.consecutive_goals_rank.size >= 3) {
                    goalFirstRank.text = "${topRanking.consecutive_goals_rank[0].rank}위"
                    goalFirstName.text = it.consecutive_goals_rank[0].username
                    goalFirstData.text = "${it.consecutive_goals_rank[0].consecutiveGoalsAchieved}일 연속"

                    goalSecondRank.text = "${topRanking.consecutive_goals_rank[1].rank}위"
                    goalSecondName.text = it.consecutive_goals_rank[1].username
                    goalSecondData.text = "${it.consecutive_goals_rank[1].consecutiveGoalsAchieved}일 연속"

                    goalThirdRank.text = "${topRanking.consecutive_goals_rank[2].rank}위"
                    goalThirdName.text = it.consecutive_goals_rank[2].username
                    goalThirdData.text = "${it.consecutive_goals_rank[2].consecutiveGoalsAchieved}일 연속"
                }
            }
        }
    }

    private fun loadRankings() {
        leaderBoardViewModel.loadMyRanking()
        leaderBoardViewModel.loadTopRankings()
    }
}