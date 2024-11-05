package com.timi.music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.MusicPlus.databinding.FragmentRegisterBinding
import com.example.utils.Logger
import com.example.utils.makeToast


class RegisterFragment : Fragment(), OnClickListener{

    companion object {
        private const val TAG = "RegisterFragment"
    }

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navigationController: NavController
    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigationController = Navigation.findNavController(view)

        observeChange()
        initListener()
    }

    /**
     * 检测RegisterViewModel中变量的变化
     */
    private fun observeChange(){
        registerViewModel.apply {
            captchaCode.observe(viewLifecycleOwner){
                if (it != 0){
                    if (it == 200){
                        Logger.i(TAG, "注册验证码发送成功.")
                        makeToast("验证码发送成功!")
                    }else{
                        Logger.i(TAG, "验证码发送失败.")
                        makeToast("验证码发送失败!请检查你的手机号是否正确!")
                    }
                    captchaCode.value = 0   //重置变量
                }
            }
        }
    }

    private fun initListener(){
        binding.ivRegisterFragmentBack.setOnClickListener(this)
        binding.btnRegisterFragmentGetCode.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.ivRegisterFragmentBack -> navigationController.navigateUp() //返回到LoginFragment
            binding.btnRegisterFragmentGetCode -> getCode()     //请求发送注册验证码
        }
    }

    /**
     * 获取手机验证码
     */
    private fun getCode(){
        if (binding.edtRegisterFragmentPhoneNumber.text.length != 11){
            //如果输入格式不满足要求，无法获取验证码
            makeToast("请输入11位手机号！")
        }else{
            registerViewModel.codeRequest(binding.edtRegisterFragmentPhoneNumber.text.toString())
        }
    }
}