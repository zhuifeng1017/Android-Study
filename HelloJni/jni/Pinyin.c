#include "Pinyin.h"
#include <stddef.h>
#include "pinyinlist.inl"

const char* ToPinyin(unsigned short wch){
	static const int MAX_MAP_SIZE = 20903;
		int low = 0, mid, high = MAX_MAP_SIZE - 1;
		while (low <= high)
		{
			mid = (low + high) / 2;
			if (pinyin_map[mid].ucs2 > wch)
			{
				high = mid - 1;
			}
			else if (pinyin_map[mid].ucs2 < wch)
			{
				low = mid + 1;
			}
			else
			{
				return pinyin_map[mid].pinyin_str;
			}
		}
		return 0;
}
