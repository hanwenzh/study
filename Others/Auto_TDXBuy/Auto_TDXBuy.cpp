
// Auto_TDXBuy.cpp : 定义应用程序的类行为。
//

#include "stdafx.h"
#include "Auto_TDXBuy.h"
#include "Auto_TDXBuyDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAuto_TDXBuyApp

BEGIN_MESSAGE_MAP(CAuto_TDXBuyApp, CWinApp)
	ON_COMMAND(ID_HELP, &CWinApp::OnHelp)
END_MESSAGE_MAP()


// CAuto_TDXBuyApp 构造

CAuto_TDXBuyApp::CAuto_TDXBuyApp()
{
	// 支持重新启动管理器
	m_dwRestartManagerSupportFlags = AFX_RESTART_MANAGER_SUPPORT_RESTART;

	// TODO: 在此处添加构造代码，
	// 将所有重要的初始化放置在 InitInstance 中
}

static char sMainClass[256] = "TdxW_MainFrame_Class";

void DoTrade(char* sRate, char * sDlgCaption, int iActionID, float fPriceOffset, char * buyCount_Default, char * sBuyBtnTxt)
{
	char sRefreshBtnTxt[256] = "刷";
	char sRateBtnTxt[256] = { 0 };

	if (_tcsicmp(sRate, _T("1")) == 0)
	{
		strcpy(sRateBtnTxt, "全部");
	}
	else
		strcpy(sRateBtnTxt, sRate);

	HWND hTDX_MainWnd = ::FindWindowA(sMainClass, NULL);
	if (hTDX_MainWnd)
	{
		HWND hTDX_QuickTradeWnd = ::FindWindowA("#32770", sDlgCaption);
		while (NULL != hTDX_QuickTradeWnd)
		{
			Sleep(10);
			SendMessage(hTDX_QuickTradeWnd, WM_CLOSE, 0, 0L);
			hTDX_QuickTradeWnd = ::FindWindowA("#32770", sDlgCaption);
		}

		/*
		买一价闪买5081买二价闪买5082买三价闪买5083买四价闪买5084买五价闪买5085
		买六价闪买5086买七价闪买5087买八价闪买5088买九价闪买5089买十价闪买5090
		卖一价闪买5091卖二价闪买5092卖三价闪买5093卖四价闪买5094卖五价闪买5095
		卖六价闪买5096卖七价闪买5097卖八价闪买5098卖九价闪买5099卖十价闪买5100
		涨停价闪买5101跌停价闪买5102
		*/
		//弹出闪电窗：
		//::SendMessage((HWND)hTDX_MainWnd, WM_COMMAND, MAKEWPARAM(5085, 0), NULL);
		::SendMessage((HWND)hTDX_MainWnd, WM_COMMAND, MAKEWPARAM(iActionID, 0), NULL);

		hTDX_QuickTradeWnd = NULL;
		while (!hTDX_QuickTradeWnd)
		{
			Sleep(100);
			hTDX_QuickTradeWnd = ::FindWindowA("#32770", sDlgCaption);
		}

		CWnd *pWnd = CWnd::FromHandle(hTDX_QuickTradeWnd)->GetWindow(GW_CHILD);

		CWnd *pCurRateBtnWnd = NULL;
		CWnd *pRefreshBtnWnd = NULL;
		TCHAR szBuf[256];
		while (pWnd != NULL)
		{
			GetClassName(pWnd->m_hWnd, szBuf, 256);
			if (_tcsicmp(szBuf, _T("Button")) == 0)
			{
				char temp1[256] = { 0 };
				pWnd->GetWindowText(temp1, 255);

				if (_tcsicmp(temp1, sRateBtnTxt) == 0)
				{
					pCurRateBtnWnd = pWnd;
					//SendMessage(pCurRateWnd->m_hWnd, BM_CLICK, 0, 0L);
				}
				else if (_tcsicmp(temp1, sRefreshBtnTxt) == 0)
				{
					pRefreshBtnWnd = pWnd;
					//SendMessage(pCurRateWnd->m_hWnd, BM_CLICK, 0, 0L);
				}
			}

			pWnd = pWnd->GetNextWindow();
		}

		pWnd = CWnd::FromHandle(hTDX_QuickTradeWnd)->GetWindow(GW_CHILD);
		int idxEdit = 0;
		int idxButton = 0;
		while (pWnd != NULL)
		{
			GetClassName(pWnd->m_hWnd, szBuf, 256);
			if (_tcsicmp(szBuf, _T("Edit")) == 0)
			{
				idxEdit++;
				if (idxEdit == 1) // 1 买入价格
				{
					// 买入价格 + 0.001
					char temp2[256] = { 0 };
					char tempNew[256] = { 0 };
					while (strlen(temp2) == 0)
					{
						Sleep(10);
						::SendMessageA(pWnd->m_hWnd, WM_GETTEXT, 256, (LPARAM)temp2);//EDIT的句柄，消息，接收缓冲区大小，接收缓冲区指针
					}
					char str[255] = { 0 };
					sprintf(str, "%.3f", atof(temp2) + fPriceOffset);
					::SendMessageA(pWnd->m_hWnd, WM_SETTEXT, 0, (LPARAM)str);

					while (_tcsicmp(tempNew, str) != 0)
					{
						Sleep(10);
						::SendMessageA(pWnd->m_hWnd, WM_GETTEXT, 256, (LPARAM)tempNew);//EDIT的句柄，消息，接收缓冲区大小，接收缓冲区指针
					}

					if (NULL != pRefreshBtnWnd)
						SendMessage(pRefreshBtnWnd->m_hWnd, BM_CLICK, 0, 0L);

					//CWnd::FromHandle(hTDX_QuickTradeWnd)->UpdateWindow();
					Sleep(200);//问题：【+ 0.001】，不知道何时新的最大可买计算结束（值也可能不变）
				}
				if (idxEdit == 3) //  3 买入数量
				{
					if (NULL == pCurRateBtnWnd)
					{
						::SendMessageA(pWnd->m_hWnd, WM_SETTEXT, 0, (LPARAM)buyCount_Default);
						char temp2[256] = { 0 };
						while (strcmp(temp2, buyCount_Default) != 0)
						{
							Sleep(10);
							::SendMessageA(pWnd->m_hWnd, WM_GETTEXT, 256, (LPARAM)temp2);//EDIT的句柄，消息，接收缓冲区大小，接收缓冲区指针
						}
					}
					else
					{
						char temp2[256] = { 0 };
						while ((strcmp(temp2, "0") == 0) || (strlen(temp2) == 0))
						{
							SendMessage(pCurRateBtnWnd->m_hWnd, BM_CLICK, 0, 0L);
							Sleep(10);
							SendMessage(pCurRateBtnWnd->m_hWnd, BM_CLICK, 0, 0L);
							Sleep(10);
							::SendMessageA(pWnd->m_hWnd, WM_GETTEXT, 256, (LPARAM)temp2);//EDIT的句柄，消息，接收缓冲区大小，接收缓冲区指针
						}

					}
				}
			}
			//else if (_tcsicmp(szBuf, _T("Button")) == 0)
			//{
			//	char temp1[256] = { 0 };
			//	pWnd->GetWindowText(temp1, 255);

			//	if (_tcsicmp(temp1, sRateBtnTxt) == 0)
			//	{
			//		pCurRateBtnWnd = pWnd;
			//		//SendMessage(pCurRateWnd->m_hWnd, BM_CLICK, 0, 0L);
			//	}
			//	else if (_tcsicmp(temp1, sRefreshBtnTxt) == 0)
			//	{
			//		pRefreshBtnWnd = pWnd;
			//		//SendMessage(pCurRateWnd->m_hWnd, BM_CLICK, 0, 0L);
			//	}
			//}

			pWnd = pWnd->GetNextWindow();
		}

		{
			HWND hWnd0 = NULL;
			while (NULL == hWnd0)
			{
				Sleep(10);
				hWnd0 = ::FindWindowExA(hTDX_QuickTradeWnd, NULL, "Button", sBuyBtnTxt);
			}
			//Sleep(100);
			while (NULL != hWnd0)
			{
				Sleep(10);
				SendMessage(hWnd0, BM_CLICK, 0, 0L);
				hWnd0 = ::FindWindowExA(hTDX_QuickTradeWnd, NULL, "Button", sBuyBtnTxt);
			}

			//委托撤单：
			//hTDX_MainWnd = ::FindWindowA(sMainClass, NULL);
			if (hTDX_MainWnd)
			{
				::SendMessage((HWND)hTDX_MainWnd, WM_COMMAND, MAKEWPARAM(5333, 0), NULL);
			}
		}
	}
}

//无法更换股票，只能交易当前主图显示行情的票
void OnBuy(char* sCode, char* sRate, float fTotalRate)
{
	char buyCount_Default[256] = "19000";
	char sDlgCaption[256] = "闪电买入";
	char sBuyBtnTxt[256] = "买 入";
	int iActionID = 5081;
	float fPriceOffset = 0.001;

	DoTrade(sRate, sDlgCaption, iActionID, fPriceOffset, buyCount_Default, sBuyBtnTxt);

}

//无法更换股票，只能交易当前主图显示行情的票
void OnSell(char* sCode, char* sRate, float fTotalRate)
{
	char buyCount_Default[256] = "19000";
	char sDlgCaption[256] = "闪电卖出";
	char sBuyBtnTxt[256] = "卖  出";
	int iActionID = 5121;
	float fPriceOffset = -0.001;

	DoTrade(sRate, sDlgCaption, iActionID, fPriceOffset, buyCount_Default, sBuyBtnTxt);
}


// 唯一的一个 CAuto_TDXBuyApp 对象

CAuto_TDXBuyApp theApp;


// CAuto_TDXBuyApp 初始化

BOOL CAuto_TDXBuyApp::InitInstance()
{
	// 如果一个运行在 Windows XP 上的应用程序清单指定要
	// 使用 ComCtl32.dll 版本 6 或更高版本来启用可视化方式，
	//则需要 InitCommonControlsEx()。  否则，将无法创建窗口。
	INITCOMMONCONTROLSEX InitCtrls;
	InitCtrls.dwSize = sizeof(InitCtrls);
	// 将它设置为包括所有要在应用程序中使用的
	// 公共控件类。
	InitCtrls.dwICC = ICC_WIN95_CLASSES;
	InitCommonControlsEx(&InitCtrls);

	CWinApp::InitInstance();


	// 创建 shell 管理器，以防对话框包含
	// 任何 shell 树视图控件或 shell 列表视图控件。
	CShellManager *pShellManager = new CShellManager;

	// 激活“Windows Native”视觉管理器，以便在 MFC 控件中启用主题
	CMFCVisualManager::SetDefaultManager(RUNTIME_CLASS(CMFCVisualManagerWindows));

	// 标准初始化
	// 如果未使用这些功能并希望减小
	// 最终可执行文件的大小，则应移除下列
	// 不需要的特定初始化例程
	// 更改用于存储设置的注册表项
	// TODO: 应适当修改该字符串，
	// 例如修改为公司或组织名
	SetRegistryKey(_T("应用程序向导生成的本地应用程序"));

	if (3 < __argc)
	{
		//OnBuy(__argv[1], __argv[2], atof(__argv[3]));
		OnSell(__argv[1], __argv[2], atof(__argv[3]));
	}
	//if ((m_lpCmdLine[0] == _T('/0')) || (lstrcmp(m_lpCmdLine, _T("b1")) != 0))
	//{
	//	m_bCmdRet = TRUE;
	//}
	//else
	//	m_bCmdRet = FALSE;
	//OnSell();
	return FALSE;
//============================================

	CAuto_TDXBuyDlg dlg;
	m_pMainWnd = &dlg;
	INT_PTR nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		// TODO: 在此放置处理何时用
		//  “确定”来关闭对话框的代码
	}
	else if (nResponse == IDCANCEL)
	{
		// TODO: 在此放置处理何时用
		//  “取消”来关闭对话框的代码
	}
	else if (nResponse == -1)
	{
		TRACE(traceAppMsg, 0, "警告: 对话框创建失败，应用程序将意外终止。\n");
		TRACE(traceAppMsg, 0, "警告: 如果您在对话框上使用 MFC 控件，则无法 #define _AFX_NO_MFC_CONTROLS_IN_DIALOGS。\n");
	}

	// 删除上面创建的 shell 管理器。
	if (pShellManager != NULL)
	{
		delete pShellManager;
	}

#ifndef _AFXDLL
	ControlBarCleanUp();
#endif

	// 由于对话框已关闭，所以将返回 FALSE 以便退出应用程序，
	//  而不是启动应用程序的消息泵。
	return FALSE;
}

