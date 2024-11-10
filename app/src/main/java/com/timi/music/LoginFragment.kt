package com.timi.music

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.timi.music.databinding.FragmentLoginBinding
import com.timi.utils.Logger
import com.timi.utils.isClickEffective
import com.timi.utils.makeToast


class LoginFragment : Fragment(), OnClickListener {

    companion object{
        private const val TAG = "LoginFragment"
    }
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        observeChange()
        initListener()
        if (Logger.currentLevel < 5){
            binding.btnLoginFragmentTest.visibility = View.GONE
        }
    }

    /**
     * 检测LoginViewModel中变量的变化
     */
    private fun observeChange(){
        loginViewModel.apply {
            //验证码发送返回结果
            captchaCode.observe(viewLifecycleOwner){
                if (it != 0){
                    if (it == 200){
                        Logger.i(TAG, "登录验证码发送成功.")
                        makeToast("验证码发送成功!")
                    }else{
                        Logger.i(TAG, "验证码发送失败.")
                        makeToast("验证码发送失败!请检查你的手机号是否正确!")
                    }
                    captchaCode.value = 0   //重置变量
                }
            }
            //游客登陆返回结果
            touristData.observe(viewLifecycleOwner){
                it?.apply {
                   if (this.code == 200){
                       //登录后跳转至主程序
                       Logger.i(TAG, "游客登陆成功.")
                       makeToast("游客登陆成功!")
                       //携带cookie跳转至CentreActivity
                       startActivity(Intent(context, CentreActivity::class.java).apply {
                           //toString防止cookie为null引发空指针异常
                           putExtra("cookie", cookie.toString())
                       })
                   }else{
                       Logger.w(TAG, "游客登陆失败.")
                       makeToast("游客登陆失败")
                   }
                   touristData.value = null
                }
            }

        }
    }

    private fun initListener(){
        binding.apply {
            btnLoginFragmentLogin.setOnClickListener(this@LoginFragment)
            btnLoginFragmentRegister.setOnClickListener(this@LoginFragment)
            btnLoginFragmentSendCode.setOnClickListener(this@LoginFragment)
            btnLoginFragmentTouristLogin.setOnClickListener(this@LoginFragment)
            binding.btnLoginFragmentTest.setOnClickListener(this@LoginFragment)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnLoginFragmentLogin -> {
                if (checkInput()){
//                    if (tryLogin()){
//
//                    }
                }
            }
            binding.btnLoginFragmentRegister -> navController.navigate(R.id.action_loginFragment_to_registerFragment)
            binding.btnLoginFragmentSendCode -> getCode()
            binding.btnLoginFragmentTouristLogin -> {
                if (v.isClickEffective()) loginViewModel.touristLoginRequest()
            }
            binding.btnLoginFragmentTest -> {
                if (v.isClickEffective()){
                    startActivity(Intent(context, CentreActivity::class.java).apply {
                        putExtra("cookie", "")
                    })
                }
            }
        }
    }

    private fun getCode(){
        if (binding.edtLoginFragmentPhoneNumber.text.length != 11){
            //如果输入格式不满足要求，无法获取验证码
            makeToast("请输入11位手机号！")
        }else{
            loginViewModel.codeRequest(binding.edtLoginFragmentPhoneNumber.text.toString())
        }
    }

    private fun checkInput(): Boolean{
        return if (binding.edtLoginFragmentPhoneNumber.text.toString().isNotEmpty()){
            if (binding.edtLoginFragmentCode.text.toString().isNotEmpty()){
                true
            }else{
                Snackbar.make(requireView(), "请输入验证码！", Snackbar.LENGTH_SHORT).show()
                false
            }
        }else{
            Snackbar.make(requireView(), "请输入手机号！", Snackbar.LENGTH_SHORT).show()
            false
        }
    }

//    private fun tryLogin(): Boolean{
//
//    }
}